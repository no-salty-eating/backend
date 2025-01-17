package com.sparta.product.application.scheduler.redis;

import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.application.exception.scheduler.TimeSaleScheduleException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisManager {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PRODUCT_ID = "product_id";
    private static final String NAME = "name";
    private static final String STOCK = "stock";
    private static final String PRICE = "price";

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
//                            .plusDays(1)    // 하루 후에 삭제 되도록 설정
                            .toInstant()
                            .toEpochMilli()
            );

        } catch (Exception e) {
            log.error("Failed to schedule timesale for product {}", timeSaleProduct.getProduct().getId(), e);
            throw new TimeSaleScheduleException();
        }
    }

    public void createHashTimeSale(TimeSaleProduct timeSaleProduct) {
        Map<String, String> timeSaleInfo = new HashMap<>();
        timeSaleInfo.put(PRODUCT_ID, timeSaleProduct.getProduct().getId().toString());
        timeSaleInfo.put(NAME, timeSaleProduct.getProduct().getName());
        timeSaleInfo.put(STOCK, timeSaleProduct.getStock().toString());
        timeSaleInfo.put(PRICE, timeSaleProduct.getDiscountPrice().toString());

        String cacheKey = RedisKeys.TIMESALE + timeSaleProduct.getProduct().getId();

        redisTemplate.opsForHash().putAll(
                cacheKey,
                timeSaleInfo
        );

//        redisTemplate.opsForValue().set(
//                cacheKey,
//                timeSaleProduct.getStock().toString()
//        );
    }

    public void createHashProduct(Product product) {
        Map<String, String> productInfo = new HashMap<>();
        productInfo.put(PRODUCT_ID, product.getId().toString());
        productInfo.put(NAME, product.getName());
        productInfo.put(STOCK, product.getStock().toString());
        productInfo.put(PRICE, product.getPrice().toString());

        String cacheKey = RedisKeys.PRODUCT + product.getId();

        redisTemplate.opsForHash().putAll(
                cacheKey,
                productInfo
        );

//        redisTemplate.opsForValue().set(
//                cacheKey,
//                product.getStock().toString()
//        );
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

    public void removeTimeSale(String productId) {
        String key = RedisKeys.TIMESALE + productId;
        redisTemplate.delete(key);
    }

    public void setExpireTime(String cacheKey, int sec) {
        redisTemplate.expire(cacheKey, Duration.ofSeconds(sec));
    }

    public boolean checkSoldOut(Long productId) {
        String key = RedisKeys.TIMESALE + productId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(key);
        String remainingStock = (String) productInfo.get(STOCK);

        return remainingStock != null && remainingStock.equals("0");
    }

    public boolean decreaseHashStock(Long productId, Integer stock, String cacheKey) {
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(cacheKey);

        if (!productInfo.isEmpty()) {
            String remainingStock = (String) productInfo.get(STOCK);
            if (remainingStock != null && Integer.parseInt(remainingStock) >= stock) {
                // 수량 감소
                int newQuantity = Integer.parseInt(remainingStock) - stock;
                redisTemplate.opsForHash().put(cacheKey, STOCK, String.valueOf(newQuantity));
                return true;
            }
        }
        return false;
    }

    public void increaseHashStock(Long productId, Integer stock, String cacheKey) {
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(cacheKey);

        if (!productInfo.isEmpty()) {
            String remainingStock = (String) productInfo.get(STOCK);
            int newQuantity = (remainingStock != null ? Integer.parseInt(remainingStock) : 0) + stock;

            redisTemplate.opsForHash().put(cacheKey, STOCK, String.valueOf(newQuantity));
        }
    }

    public void updateProductHash(Product product) {
        String cacheKey = RedisKeys.PRODUCT + product.getId();
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(cacheKey);

        if (!productInfo.isEmpty()) {
            redisTemplate.opsForHash().put(cacheKey, NAME, product.getName());
            redisTemplate.opsForHash().put(cacheKey, PRICE, product.getPrice().toString());
        }
    }
}
