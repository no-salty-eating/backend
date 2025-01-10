package com.sparta.product.application.scheduler.redis;

import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.application.exception.scheduler.TimeSaleScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
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
            redisTemplate.opsForHash().put(RedisKeys.TIMESALE_INVENTORY,
                    timeSaleProduct.getId().toString(),
                    timeSaleProduct.getQuantity().toString());
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

    public void removeStartSchedule(String timeSaleProductId) {
        redisTemplate.opsForZSet().remove(RedisKeys.TIMESALE_START_KEY, timeSaleProductId);
    }

    public void removeEndSchedule(String timeSaleProductId) {
        redisTemplate.opsForZSet().remove(RedisKeys.TIMESALE_END_KEY, timeSaleProductId);
    }

    public void removeInventory(String timeSaleProductId) {
        String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
        redisTemplate.opsForHash().delete(inventoryKey, timeSaleProductId);
    }

    public boolean checkInventoryQuantity(Long timeSaleProductId, Integer quantity) {
        String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
        String remainingQuantity = (String) redisTemplate.opsForHash().get(inventoryKey, timeSaleProductId.toString());

        return remainingQuantity != null && Integer.parseInt(remainingQuantity) >= quantity;
    }

    public boolean decreaseInventory(Long timeSaleProductId, Integer quantity) {
        if (checkInventoryQuantity(timeSaleProductId, quantity)) {
            String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
            redisTemplate.opsForHash().increment(inventoryKey, timeSaleProductId.toString(), -Long.valueOf(quantity));
            return true;
        } else {
            return false;
        }
    }

    public void increaseInventory(Long timeSaleProductId, Integer quantity) {
        String inventoryKey = RedisKeys.TIMESALE_INVENTORY;
        redisTemplate.opsForHash().increment(inventoryKey, timeSaleProductId.toString(), Long.valueOf(quantity));
    }
}
