package com.sparta.product.application.service.redis;

import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.application.exception.scheduler.TimeSaleScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeSaleRedisManager {
    private final RedisTemplate<String, String> redisTemplate;

    public void scheduleTimeSale(TimeSaleProduct timeSaleProduct) {
        try {
            // 시작 시간 스케줄링
            redisTemplate.opsForZSet().add(
                    RedisKeys.TIMESALE_START_KEY,
                    timeSaleProduct.getId().toString(),
                    timeSaleProduct.getTimeSaleStartTime().atZone(ZoneId.of("Asia/Seoul"))
                            .toInstant()
                            .toEpochMilli()
            );

            // 종료 시간 스케줄링
            redisTemplate.opsForZSet().add(
                    RedisKeys.TIMESALE_END_KEY,
                    timeSaleProduct.getId().toString(),
                    timeSaleProduct.getTimeSaleEndTime().atZone(ZoneId.of("Asia/Seoul"))
                            .toInstant()
                            .toEpochMilli()
            );

            // 재고 초기화
            redisTemplate.opsForHash().put(RedisKeys.TIMESALE_INVENTORY, timeSaleProduct.getId().toString(), timeSaleProduct.getQuantity().toString());
        } catch (Exception e) {
            log.error("Failed to schedule timesale for product {}", timeSaleProduct.getId(), e);
            throw new TimeSaleScheduleException();
        }
    }

    public Set<String> getStartTimeSales(long currentTime) {
        return redisTemplate.opsForZSet().rangeByScore(
                RedisKeys.TIMESALE_START_KEY,
                0,
                currentTime
        );
    }

    public Set<String> getEndTimeSales(long currentTime) {
        return redisTemplate.opsForZSet().rangeByScore(
                RedisKeys.TIMESALE_END_KEY,
                0,
                currentTime
        );
    }

    public void removeStartSchedule(String productId) {
        redisTemplate.opsForZSet().remove(RedisKeys.TIMESALE_START_KEY, productId);
    }

    public void removeEndSchedule(String productId) {
        redisTemplate.opsForZSet().remove(RedisKeys.TIMESALE_END_KEY, productId);
    }

    public void removeInventory(String productId) {
        String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
        redisTemplate.opsForHash().delete(inventoryKey, productId);
    }

    public boolean decreaseInventory(Long productId, Integer quantity) {
        String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
        Long remainingQuantity = (Long) redisTemplate.opsForHash().get(inventoryKey, productId.toString());

        if (remainingQuantity != null && remainingQuantity >= quantity) {
            redisTemplate.opsForHash().increment(inventoryKey, productId.toString(), -quantity);
            return true;
        } else {
            // 재고가 부족한 경우 원복
            return false;
        }
    }

    public void increaseInventory(Long productId, Integer quantity) {
        String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
        redisTemplate.opsForHash().increment(inventoryKey, productId.toString(), quantity);
    }
}
