package com.sparta.product.application.service;

import com.sparta.product.application.dtos.timesale.TimeSaleProductRequestDto;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.application.exception.product.NotFoundProductException;
import com.sparta.product.application.exception.timesale.*;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.domain.core.TimeSaleSoldOut;
import com.sparta.product.domain.repository.ProductRepository;
import com.sparta.product.domain.repository.TimeSaleProductRepository;
import com.sparta.product.application.service.redis.RedisKeys;
import com.sparta.product.application.service.redis.TimeSaleRedisManager;
import com.sparta.product.domain.repository.TimeSaleSoldOutRepository;
import com.sparta.product.presentation.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSaleService {

    private final TimeSaleProductRepository timeSaleProductRepository;
    private final ProductRepository productRepository;
    private final TimeSaleSoldOutRepository timeSaleSoldOutRepository;
    private final TimeSaleRedisManager redisManager;
    private final TimeSaleSchedulerService timeSaleSchedulerService;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public Response<Void> createTimeSaleProduct(TimeSaleProductRequestDto timeSaleProductRequestDto, String role) {
        checkIsMaster(role);

        Long productId = timeSaleProductRequestDto.productId();
        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        validateTimeSaleRequest(timeSaleProductRequestDto, product);

        TimeSaleProduct timeSaleProduct = TimeSaleProduct.createOf(
                timeSaleProductRequestDto.discountRate(),
                timeSaleProductRequestDto.quantity(),
                timeSaleProductRequestDto.timeSaleStartTime(),
                timeSaleProductRequestDto.timeSaleEndTime(),
                product);
        timeSaleProductRepository.save(timeSaleProduct);

        redisManager.scheduleTimeSale(timeSaleProduct);

        return Response.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .build();
    }

    private void validateTimeSaleRequest(TimeSaleProductRequestDto request, Product product) {
        // 수량 체크
        if (request.quantity() > product.getStock()) {
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
    }

    @Transactional
    public Response<Void> purchaseTimeSaleProduct(Long productId, Integer quantity) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findByIdAndIsDeletedFalseAndIsPublicTrue(productId)
                .orElseThrow(NotFoundOnTimeSaleException::new);

        // Redis를 통한 재고 확인 및 감소..
        if (!redisManager.decreaseInventory(productId, quantity)) {
            throw new ExceedTimeSaleQuantityException();
        }

        try {
            // 실제 구매 처리 로직
            processTimeSalePurchaseAsyncToDB(timeSaleProduct, quantity);

            // 재고 소진 시 타임세일 종료 처리
            // TODO : redis와 DB의 수량을 어떻게 일치시킬지? kafka 이용....?
            if (isStockEmpty(productId)) {
                timeSaleProduct.updateIsPublic(false);
                timeSaleProduct.updateIsSoldOut(true);
                timeSaleProduct.getProduct().updateIsPublic(true);
                timeSaleSoldOutRepository.save(TimeSaleSoldOut.createFrom(timeSaleProduct));
                timeSaleSchedulerService.endTimeSale(productId);
            }

            return Response.<Void>builder().build();

        } catch (Exception e) {
            // 구매 실패 시 Redis 재고 원복
            redisManager.increaseInventory(productId, quantity);
            // TODO : 예외 만들기
            throw e;
        }
    }

    private boolean isStockEmpty(Long productId) {
        String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
        String remainingStock = (String) redisTemplate.opsForHash().get(inventoryKey, productId.toString());
        return remainingStock == null || Integer.parseInt(remainingStock) <= 0;
    }

    @Async
    @Transactional
    protected void processTimeSalePurchaseAsyncToDB(TimeSaleProduct timeSaleProduct, Integer quantity) {
        try {
            Product product = timeSaleProduct.getProduct();
            // 재고 감소
            timeSaleProduct.decreaseQuantity(quantity);
            product.decreaseStock(quantity);

            log.info("Processed TimeSale purchase asynchronously - timeSaleId: {}, quantity: {}", timeSaleProduct.getId(), quantity);
        } catch (Exception e) {
            // Redis 재고 복구
            redisManager.increaseInventory(timeSaleProduct.getId(), quantity);
            log.error("Failed to process TimeSale purchase, compensating Redis inventory - timeSaleId: {}", timeSaleProduct.getId(), e);
            // TODO : 예외 만들기
            throw e;
        }
    }

    private void checkIsMaster(String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString())) {
            log.info("forbidden role in checkIsMaster: {}", role);
            throw new ForbiddenRoleException();
        }
    }
}
