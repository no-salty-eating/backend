package com.study.order.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.order.application.dto.response.ProductData
import com.study.order.application.dto.response.ProductResponseDto
import com.study.order.application.exception.AcquireLockTimeoutException
import com.study.order.application.exception.InternalServerError
import com.study.order.application.service.CacheService
import com.study.order.infrastructure.config.log.KEY_TXID
import com.study.order.infrastructure.config.log.LoggerProvider
import com.study.order.infrastructure.utils.TransactionHelper
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.withContext
import org.redisson.api.RedissonReactiveClient
import org.slf4j.MDC
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class CacheService(
    private val mapper: ObjectMapper,
    private val transactionHelper: TransactionHelper,
    private val redissonClient: RedissonReactiveClient,
    redisTemplate: ReactiveRedisTemplate<String, String>,
) : CacheService {

    companion object {
        private const val PRODUCT_KEY = "product:"
        private const val TIME_SALE_KEY = "timeSale:"
        private const val REDISSON_KEY_PREFIX = "lock_"
        private const val PRODUCT_STOCK_KEY = "stock"
        private const val ORDER_KEY = "order:"
        private const val TRY_LOCK_TIME_OUT = 5L
        private const val LEASE_TIME = 4L
        private const val TARGET_METHOD_TIME_OUT = 3L
    }

    private val logger = LoggerProvider.logger
    private val opsForValue = redisTemplate.opsForValue()
    private val opsForHash = redisTemplate.opsForHash<String, String>()

    override suspend fun getProductInfo(productId: Long): ProductResponseDto? {
        return getHash(productId)
    }

    override suspend fun decrementStock(productId: Long, amount: Int, isTimeSale: Boolean) {
        updateStock(productId, amount, false, isTimeSale)
    }

    override suspend fun incrementStock(productId: Long, amount: Int, isTimeSale: Boolean) {
        updateStock(productId, amount, true, isTimeSale)
    }

    override suspend fun executeWithLock(productIds: List<Long>, runner: suspend () -> Unit) {

        val txid = MDC.get(KEY_TXID).toLong()
        val locks = productIds.map { redissonClient.getLock(REDISSON_KEY_PREFIX + PRODUCT_KEY + it) }

        try {
            locks.forEach {
                check(it.tryLock(TRY_LOCK_TIME_OUT, LEASE_TIME, TimeUnit.SECONDS, txid).awaitSingle()) {
                    throw AcquireLockTimeoutException()
                }
            }

            transactionHelper.executeInNewTransaction(TARGET_METHOD_TIME_OUT) {
                runner()
            }
        } catch (ex: Exception) {
            when (ex) {
                is TimeoutCancellationException -> throw AcquireLockTimeoutException()
                else -> throw InternalServerError()
            }
        } finally {
            withContext(NonCancellable) {
                locks.forEach {
                    it.unlock(txid).awaitSingleOrNull()
                }
            }
        }
    }

    override suspend fun saveOrderInfo(productId: Long, isTimeSale: Boolean) {
        val key = ORDER_KEY + if (isTimeSale) TIME_SALE_KEY + productId else PRODUCT_KEY + productId

        opsForValue.set(key, isTimeSale.toString().uppercase()).awaitSingle()
    }

    override suspend fun isTimeSaleOrder(productId: Long): Boolean {
        return opsForValue.get(ORDER_KEY + TIME_SALE_KEY + productId).awaitSingleOrNull() != null
    }

    override suspend fun deleteOrderInfo(productId: Long, isTimeSale: Boolean) {
        val key = ORDER_KEY + if (isTimeSale) TIME_SALE_KEY + productId else PRODUCT_KEY + productId

        opsForValue.delete(key).awaitSingle()
    }

    private suspend fun getHash(productId: Long): ProductResponseDto? {
        val keys = listOf(TIME_SALE_KEY + productId, PRODUCT_KEY + productId)

        for (key in keys) {
            val entries = opsForHash.entries(key).asFlow().toList()
            if (entries.isNotEmpty()) {
                val productData = mapper.writeValueAsString(entries.associate { it.key to it.value })
                return convertDataAsDto(productData, key.startsWith(TIME_SALE_KEY))
            }
        }
        return null
    }

    private fun convertDataAsDto(record: String, isTimeSale: Boolean): ProductResponseDto {
        return toProductResponseDto(mapper.readValue(record, ProductData::class.java), isTimeSale)
    }

    private fun toProductResponseDto(record: ProductData, isTimeSale: Boolean): ProductResponseDto {
        return ProductResponseDto.from(record, isTimeSale)
    }

    private suspend fun updateStock(productId: Long, amount: Int, isIncrement: Boolean, isTimeSale: Boolean) {
        val key = if (isTimeSale) TIME_SALE_KEY + productId else PRODUCT_KEY + productId

        val stock = opsForHash.get(key, PRODUCT_STOCK_KEY).awaitSingle().toInt()
        val newStock = if (isIncrement) stock + amount else stock - amount

        opsForHash.put(key, PRODUCT_STOCK_KEY, newStock.toString()).awaitSingle()
    }
}