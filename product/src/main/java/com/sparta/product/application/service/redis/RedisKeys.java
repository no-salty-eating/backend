package com.sparta.product.application.service.redis;

import org.springframework.stereotype.Component;

@Component
public class RedisKeys {
    public static final String TIMESALE_START_KEY = "timesale:start";
    public static final String TIMESALE_END_KEY = "timesale:end";
    public static final String TIMESALE_INVENTORY = "timesale:inventory";
}
