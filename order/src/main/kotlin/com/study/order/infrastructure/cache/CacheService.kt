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

private const val PRODUCT_KEY = "product:"
private const val TIME_SALE_KEY = "timeSale:"
private const val PRODUCT_ORDER_KEY = "product:order:"
private const val TIME_SALE_ORDER_KEY = "timeSale:order:"

@Service
class CacheService(
    redisTemplate: ReactiveRedisTemplate<String, String>,
    private val mapper: ObjectMapper,
) : CacheService {

    private val logger = LoggerProvider.logger
    private val opsForValue = redisTemplate.opsForValue()
    private val opsForHash = redisTemplate.opsForHash<String, String>()

    override suspend fun getProductInfo(productId: Long): ProductResponseDto? {
        return getHash(productId)
    }

    override suspend fun increment(productId: Long, quantity: Int) {
        opsForValue.increment(TIME_SALE_ORDER_KEY + productId, quantity.toLong()).awaitSingleOrNull()
    }

    override suspend fun decrement(productId: Long, quantity: Int) {
        opsForValue.decrement(TIME_SALE_ORDER_KEY + productId, quantity.toLong()).awaitSingleOrNull()
    }

    override suspend fun getSoldQuantity(productId: Long): Int? {
        return opsForValue.get(TIME_SALE_ORDER_KEY + productId).awaitSingleOrNull()?.toInt()
            ?: opsForValue.get(PRODUCT_ORDER_KEY + productId).awaitSingleOrNull()?.toInt()
    }

    private suspend fun getHash(productId: Long): ProductResponseDto? {
        val keys = listOf(TIME_SALE_KEY + productId, PRODUCT_KEY + productId)

        for (key in keys) {
            val entries = opsForHash.entries(key).asFlow().toList()
            if (entries.isNotEmpty()) {
                val productData = mapper.writeValueAsString(entries.associate { it.key to it.value })
                return convertDataAsDto(productData)
            }
        }
        return null
    }

    private fun convertDataAsDto(record: String): ProductResponseDto {
        return toProductResponseDto(mapper.readValue(record, ProductData::class.java))
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