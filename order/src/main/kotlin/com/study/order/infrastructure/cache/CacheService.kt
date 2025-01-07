package com.study.order.infrastructure.cache

import com.study.order.application.service.CacheService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service


@Service
class CacheService(
    redisTemplate: ReactiveRedisTemplate<Any, Any>,
) : CacheService {

    private val template = redisTemplate.opsForValue()

}