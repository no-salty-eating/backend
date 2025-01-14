package com.sparta.product.application.scheduler.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeys {
    public static final String TIMESALE_START_KEY = "timeSale:start";
    public static final String TIMESALE_END_KEY = "timeSale:end";
    public static final String TIMESALE = "timeSale:";
    public static final String PRODUCT = "product:";
    public static final String PRODUCT_DETAIL = "product-detail:";
}
