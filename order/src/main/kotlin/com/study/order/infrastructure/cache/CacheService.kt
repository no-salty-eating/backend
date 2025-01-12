package com.study.order.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.order.application.dto.response.ProductData
import com.study.order.application.dto.response.ProductResponseDto
import com.study.order.application.service.CacheService
import com.study.order.infrastructure.config.log.LoggerProvider
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service

private const val PRODUCT_KEY = "product::"
private const val TIME_SALE_KEY = "timesale:on:"
private const val TIME_SALE_ORDER_KEY = "timesale:order:"

@Service
class CacheService(
    redisTemplate: ReactiveRedisTemplate<String, String>,
    private val mapper: ObjectMapper,
) : CacheService {

    private val logger = LoggerProvider.logger
    private val opsForValue = redisTemplate.opsForValue()
    private val opsForHash = redisTemplate.opsForHash<String, String>()

    override suspend fun get(productId: Long): ProductResponseDto? {

        return getHash(productId) ?: getValue(productId)
    }

    override suspend fun increment(productId: Long) {
        opsForValue.increment(TIME_SALE_ORDER_KEY + productId).awaitSingleOrNull()
    }

    override suspend fun decrement(productId: Long) {
        opsForValue.decrement(TIME_SALE_ORDER_KEY + productId).awaitSingleOrNull()
    }

    override suspend fun getSize(productId: Long): Int? {
        return opsForValue.get(TIME_SALE_ORDER_KEY + productId).awaitSingleOrNull()?.toInt()
    }

    private suspend fun getHash(productId: Long): ProductResponseDto? {
        return opsForHash.entries(TIME_SALE_KEY + productId).asFlow().toList().takeIf { it.isNotEmpty() }
            ?.associate { it.key to it.value }
            ?.let { convertDataAsDto(mapper.writeValueAsString(it)) }
    }

    private suspend fun getValue(productId: Long): ProductResponseDto? {
        return opsForValue.get(PRODUCT_KEY + productId).map { toProductResponseDto(it as String) }.awaitSingleOrNull()
    }

    private fun convertDataAsDto(record: String): ProductResponseDto {
        return toProductResponseDto(mapper.readValue(record, ProductData::class.java))
    }

    private fun toProductResponseDto(record: String): ProductResponseDto {
        return mapper.readValue(record, ProductResponseDto::class.java)
    }

    private fun toProductResponseDto(record: ProductData): ProductResponseDto {
        return ProductResponseDto(
            record.productId.toLong(),
            record.name,
            record.price.toInt(),
            record.stock.toInt()
        )
    }
}