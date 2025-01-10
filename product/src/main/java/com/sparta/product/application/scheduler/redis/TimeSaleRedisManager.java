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
                    timeSaleProduct.getProduct().getId().toString(),
                    timeSaleProduct.getTimeSaleStartTime().atZone(ZoneId.of("Asia/Seoul"))
                            .toInstant()
                            .toEpochMilli()
            );

            // 종료 시간 스케줄링
            redisTemplate.opsForZSet().add(
                    RedisKeys.TIMESALE_END_KEY,
                    timeSaleProduct.getProduct().getId().toString(),
                    timeSaleProduct.getTimeSaleEndTime().atZone(ZoneId.of("Asia/Seoul"))
                            .toInstant()
                            .toEpochMilli()
            );

        } catch (Exception e) {
            log.error("Failed to schedule timesale for product {}", timeSaleProduct.getProduct().getId(), e);
            throw new TimeSaleScheduleException();
        }
    }

    public void createTimeSaleProduct(TimeSaleProduct timeSaleProduct) {
        Map<String, String> productInfo = new HashMap<>();
        productInfo.put("product_id", timeSaleProduct.getProduct().getId().toString());
        productInfo.put("name", timeSaleProduct.getProduct().getName());
        productInfo.put("stock", timeSaleProduct.getStock().toString());
        productInfo.put("price", timeSaleProduct.getDiscountPrice().toString());

        redisTemplate.opsForHash().putAll(
                RedisKeys.TIMESALE_ON + timeSaleProduct.getProduct().getId(),
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

    public void removeStartSchedule(String productId) {
        redisTemplate.opsForZSet().remove(RedisKeys.TIMESALE_START_KEY, productId);
    }

    public void removeEndSchedule(String productId) {
        redisTemplate.opsForZSet().remove(RedisKeys.TIMESALE_END_KEY, productId);
    }

    public void removeTimeSaleOn(String productId) {
        String key = RedisKeys.TIMESALE_ON + productId;
        redisTemplate.delete(key);
    }

    public boolean checkSoldOut(Long productId) {
        String key = RedisKeys.TIMESALE_ON + productId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);
        String remainingStock = (String) productInfo.get("stock");

        return remainingStock != null && remainingStock.equals("0");
    }

    public boolean decreaseInventory(Long productId, Integer stock) {
        String key = RedisKeys.TIMESALE_ON + productId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);

        if (!productInfo.isEmpty()) {
            String remainingStock = (String) productInfo.get("stock");
            if (remainingStock != null && Integer.parseInt(remainingStock) >= stock) {
                // 수량 감소
                int newQuantity = Integer.parseInt(remainingStock) - stock;
                redisTemplate.opsForHash().put(key, "stock", String.valueOf(newQuantity));
                return true;
            }
        }
        return false;
    }

    public void increaseInventory(Long productId, Integer stock) {
        String key = RedisKeys.TIMESALE_ON + productId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);

        if (!productInfo.isEmpty()) {
            String remainingStock = (String) productInfo.get("stock");
            int newQuantity = (remainingStock != null ? Integer.parseInt(remainingStock) : 0) + stock;

            redisTemplate.opsForHash().put(key, "stock", String.valueOf(newQuantity));
        }
    }
}
