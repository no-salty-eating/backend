package com.study.payment.infrastructure.cache

import com.study.payment.application.service.CacheService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

@Service
class CacheService(
    redisTemplate: ReactiveRedisTemplate<Any, Any>,
    @Value("\${spring.profiles.active:local}")
    profile: String,
) : CacheService {

    private val template = redisTemplate.opsForSet()
    private val key = "$profile/status-capture"

    override suspend fun put(paymentId: Long) {
        template.add(key, paymentId).awaitSingleOrNull()
    }

    override suspend fun remove(paymentId: Long) {
        template.remove(key, paymentId).awaitSingleOrNull()
    }

    override suspend fun getAll(): List<Long> {
        return template.members(key).asFlow().map { it as Long }.toList()
    }

}