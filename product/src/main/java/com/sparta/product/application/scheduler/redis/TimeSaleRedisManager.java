package com.sparta.product.application.scheduler.redis;

import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.application.exception.scheduler.TimeSaleScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
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

        } catch (Exception e) {
            log.error("Failed to schedule timesale for product {}", timeSaleProduct.getId(), e);
            throw new TimeSaleScheduleException();
        }
    }

    public void createTimeSaleProduct(TimeSaleProduct timeSaleProduct) {
        Map<String, String> productInfo = new HashMap<>();
        productInfo.put("timesale_id", timeSaleProduct.getId().toString());
        productInfo.put("product_id", timeSaleProduct.getProduct().getId().toString());
        productInfo.put("quantity", timeSaleProduct.getQuantity().toString());
        productInfo.put("discount_price", timeSaleProduct.getDiscountPrice().toString());

        redisTemplate.opsForHash().putAll(
                RedisKeys.TIMESALE_ON + timeSaleProduct.getId(),
                productInfo
        );
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

    public void removeTimeSaleOn(String timeSaleProductId) {
        String key = RedisKeys.TIMESALE_ON + timeSaleProductId;
        redisTemplate.delete(key);
    }

    public boolean checkInventoryQuantity(Long timeSaleProductId, Integer quantity) {
        String key = RedisKeys.TIMESALE_ON + timeSaleProductId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);
        String remainingQuantity = (String) productInfo.get("quantity");

        return remainingQuantity != null && Integer.parseInt(remainingQuantity) >= quantity;
    }

    public boolean decreaseInventory(Long timeSaleProductId, Integer quantity) {
        String key = RedisKeys.TIMESALE_ON + timeSaleProductId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);

        if (!productInfo.isEmpty()) {
            String remainingQuantity = (String) productInfo.get("quantity");
            if (remainingQuantity != null && Integer.parseInt(remainingQuantity) >= quantity) {
                // 수량 감소
                int newQuantity = Integer.parseInt(remainingQuantity) - quantity;
                redisTemplate.opsForHash().put(key, "quantity", String.valueOf(newQuantity));
                return true;
            }
        }
        return false;
    }

    public void increaseInventory(Long timeSaleProductId, Integer quantity) {
        String key = RedisKeys.TIMESALE_ON + timeSaleProductId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);

        if (!productInfo.isEmpty()) {
            String remainingQuantity = (String) productInfo.get("quantity");
            int newQuantity = (remainingQuantity != null ? Integer.parseInt(remainingQuantity) : 0) + quantity;

            redisTemplate.opsForHash().put(key, "quantity", String.valueOf(newQuantity));
        }
    }
}
