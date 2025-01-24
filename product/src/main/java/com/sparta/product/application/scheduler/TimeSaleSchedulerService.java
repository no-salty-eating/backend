package com.sparta.product.application.scheduler;

import com.sparta.product.application.exception.timesale.NotFoundOnTimeSaleException;
import com.sparta.product.application.scheduler.redis.RedisManager;
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
    private final RedisManager redisManager;

    @Transactional
    public void startTimeSale(Long productId) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findByProductIdAndIsDeletedFalseAndIsPublicTrue(productId)
                .orElseThrow(NotFoundOnTimeSaleException::new);

        timeSaleProduct.updateIsPublic(true);
    }

    @Transactional
    public void endTimeSale(Long productId) {
        TimeSaleProduct timeSaleProduct = timeSaleProductRepository.findByProductIdAndIsDeletedFalseAndIsPublicTrue(productId)
                .orElseThrow(NotFoundOnTimeSaleException::new);

//        timeSaleRedisManager.removeTimeSale(productId.toString());
//        timeSaleRedisManager.removeEndSchedule(productId.toString());

        timeSaleProduct.updateIsPublic(false);
        timeSaleProduct.getProduct().updateIsPublic(true);
    }
}
