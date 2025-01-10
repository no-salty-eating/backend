package com.sparta.product.application.scheduler;

import com.sparta.product.application.exception.timesale.NotFoundTimeSaleException;
import com.sparta.product.application.scheduler.redis.TimeSaleRedisManager;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.domain.repository.TimeSaleProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSaleSchedulerService {

    private final TimeSaleProductRepository timeSaleProductRepository;
    private final TimeSaleRedisManager timeSaleRedisManager;

    @Transactional
    public void startTimeSale(Long productId) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findById(productId)
                .orElseThrow(NotFoundTimeSaleException::new);

        Product product = timeSaleProduct.getProduct();

        // 상품과 타임세일 상품의 공개 여부 업데이트
        product.updateIsPublic(false);
        timeSaleProduct.updateIsPublic(true);

        log.info("TimeSale started - productId: {}, timeSaleId: {}", product.getId(), timeSaleProduct.getId());
    }

    @Transactional
    public void endTimeSale(Long productId) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findById(productId)
                .orElseThrow(NotFoundTimeSaleException::new);

        timeSaleRedisManager.removeInventory(productId.toString());
        timeSaleRedisManager.removeEndSchedule(productId.toString());

        Product product = timeSaleProduct.getProduct();

        // 상품과 타임세일 상품의 공개 여부 업데이트
        product.updateIsPublic(true);
        timeSaleProduct.updateIsPublic(false);

        log.info("TimeSale ended - productId: {}, timeSaleId: {}", product.getId(), timeSaleProduct.getId());
    }
}
