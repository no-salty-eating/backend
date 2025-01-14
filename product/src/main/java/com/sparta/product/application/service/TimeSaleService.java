package com.sparta.product.application.service;

import com.sparta.product.application.dtos.timesale.TimeSaleProductRequestDto;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.application.exception.product.NotFoundProductException;
import com.sparta.product.application.exception.timesale.*;
import com.sparta.product.application.scheduler.TimeSaleSchedulerService;
import com.sparta.product.application.scheduler.redis.RedisKeys;
import com.sparta.product.application.scheduler.redis.RedisManager;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.domain.core.TimeSaleSoldOut;
import com.sparta.product.domain.repository.ProductRepository;
import com.sparta.product.domain.repository.TimeSaleProductRepository;
import com.sparta.product.domain.repository.TimeSaleSoldOutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSaleService {

    private final TimeSaleProductRepository timeSaleProductRepository;
    private final ProductRepository productRepository;
    private final TimeSaleSoldOutRepository timeSaleSoldOutRepository;
    private final RedisManager redisManager;
    private final TimeSaleSchedulerService timeSaleSchedulerService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String STOCK = "stock";

    @Transactional
    public void createTimeSaleProduct(TimeSaleProductRequestDto timeSaleProductRequestDto, String role) {
        checkIsMaster(role);

        Long productId = timeSaleProductRequestDto.productId();
        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        validateTimeSaleRequest(timeSaleProductRequestDto, product);

        TimeSaleProduct timeSaleProduct = TimeSaleProduct.createOf(
                timeSaleProductRequestDto.discountRate(),
                timeSaleProductRequestDto.stock(),
                timeSaleProductRequestDto.timeSaleStartTime(),
                timeSaleProductRequestDto.timeSaleEndTime(),
                product);
        timeSaleProductRepository.save(timeSaleProduct);

        redisManager.scheduleTimeSale(timeSaleProduct);
    }

    @Transactional
    public void decreaseTimeSaleProductInRedis(Long productId, Integer stock) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findByProductIdAndIsDeletedFalseAndIsPublicTrue(productId)
                .orElseThrow(NotFoundOnTimeSaleException::new);

        // 재고 소진
        // TODO : redis와 DB의 수량을 어떻게 일치시킬지? kafka 이용....?
        // TODO : 현재는 redis와 일치하지 않는 문제도 있고, db에는 음수로도 값이 들어감
        if (isStockEmptyInRedis(productId)) {
            timeSaleProduct.updateIsSoldOut(true);
            timeSaleSoldOutRepository.save(TimeSaleSoldOut.createFrom(timeSaleProduct));
            timeSaleSchedulerService.endTimeSale(productId);
            throw new EmptyStockException();
        }

        String cacheKey = RedisKeys.TIMESALE + productId;

        // 감소
        if (!redisManager.decreaseHashStock(productId, stock, cacheKey)) {
            throw new ExceedTimeSaleQuantityException();
        }
    }

    @Transactional
    public void decreaseTimeSaleStockInDB(Long productId, Integer quantity) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findByProductIdAndIsDeletedFalseAndIsPublicTrue(productId)
                .orElseThrow(NotFoundOnTimeSaleException::new);

        if (timeSaleProduct.getStock() <= 0 || timeSaleProduct.getStock() < quantity) {
            throw new NotEnoughTimeSaleStockInDbException();
        }

        timeSaleProduct.decreaseStock(quantity);
    }

    @Transactional
    public void increaseTimeSaleStockInDb(Long productId, Integer quantity) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findByProductIdAndIsDeletedFalseAndIsPublicTrue(productId)
                .orElseThrow(NotFoundOnTimeSaleException::new);

        timeSaleProduct.increaseStock(quantity);
    }

    public boolean isEmptyTimeSaleInRedis(Long productId) {
        String cacheKey = RedisKeys.TIMESALE + productId;
        Map<Object, Object> timeSaleInfo = redisTemplate.opsForHash().entries(cacheKey);

        return timeSaleInfo.isEmpty();
    }

    private boolean isStockEmptyInRedis(Long productId) {
        String key = RedisKeys.TIMESALE + productId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);

        String remainingStock = (String) productInfo.get(STOCK);
        return remainingStock == null || Integer.parseInt(remainingStock) <= 0;
    }

    private void validateTimeSaleRequest(TimeSaleProductRequestDto request, Product product) {
        // 수량 체크
        if (request.stock() > product.getStock()) {
            throw new TimeSaleQuantityExceedProductStockException();
        }

        // 시간 유효성 체크
        LocalDateTime now = LocalDateTime.now();
        if (request.timeSaleStartTime().isBefore(now)) {
            throw new InvalidTimeSaleStartTimeException();
        }
        if (request.timeSaleEndTime().isBefore(request.timeSaleStartTime())) {
            throw new InvalidTimeSaleEndTimeException();
        }

        // 중복 타임세일 체크
        if (timeSaleProductRepository.existsByProductIdAndTimeSaleEndTimeAfter(product.getId(), now)) {
            throw new DuplicateTimeSaleException();
        }
    }

    private void checkIsMaster(String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString())) {
            log.info("forbidden role in checkIsMaster: {}", role);
            throw new ForbiddenRoleException();
        }
    }
}
