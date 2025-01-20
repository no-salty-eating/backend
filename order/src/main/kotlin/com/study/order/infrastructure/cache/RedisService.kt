package com.study.order.infrastructure.cache

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.order.application.dto.response.ProductData
import com.study.order.application.dto.response.ProductResponseDto
import com.study.order.application.exception.NotEnoughStockException
import com.study.order.application.service.CacheService
import com.study.order.infrastructure.config.log.LoggerProvider
import com.study.order.infrastructure.utils.TransactionHelper
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.redisson.api.RScript
import org.redisson.api.RedissonReactiveClient
import org.redisson.client.codec.StringCodec
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service


@Service
class RedisService(
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
        val keys = listOf(TIME_SALE_KEY + productId, PRODUCT_KEY + productId)

        for (key in keys) {
            val entries = opsForHash.entries(key).asFlow().toList()
            if (entries.isNotEmpty()) {
                val productData = mapper.writeValueAsString(entries.associate { it.key to it.value })
                val convertDataAsDto = convertDataAsDto(productData, key.startsWith(TIME_SALE_KEY))

                if (convertDataAsDto.stock <= 0) {
                    throw NotEnoughStockException()
                }
                return convertDataAsDto
            }
        }
        return null
    }

    override suspend fun decrementStock(productId: Long, amount: Int, isTimeSale: Boolean) {
        updateStock(productId, amount, false, isTimeSale)
    }

    override suspend fun incrementStock(productId: Long, amount: Int, isTimeSale: Boolean) {
        updateStock(productId, amount, true, isTimeSale)
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

        logger.debug { "key : $key" }
        opsForValue.delete(key).awaitSingle()
    }

    private fun convertDataAsDto(record: String, isTimeSale: Boolean): ProductResponseDto {
        return toProductResponseDto(mapper.readValue(record, ProductData::class.java), isTimeSale)
    }

    private fun toProductResponseDto(record: ProductData, isTimeSale: Boolean): ProductResponseDto {
        return ProductResponseDto.from(record, isTimeSale)
    }

    private suspend fun updateStock(productId: Long, amount: Int, isIncrement: Boolean, isTimeSale: Boolean) {
        val key = (if (isTimeSale) TIME_SALE_KEY else PRODUCT_KEY) + productId

        val luaScript = """
            local key = KEYS[1]
            local productStockKey = ARGV[1]
            local amount = tonumber(ARGV[2])
            local isIncrement = ARGV[3]

            local stock = tonumber(redis.call('hget', key, productStockKey))

            local newStock
            if isIncrement == "true" then
                newStock = stock + amount
            else
                newStock = stock - amount
            end

            redis.call('hset', key, productStockKey, tostring(newStock))

            return newStock
        
        """.trimIndent()

        redissonClient.getScript(StringCodec.INSTANCE).eval<Any>(
            RScript.Mode.READ_WRITE,
            luaScript,
            RScript.ReturnType.INTEGER,
            listOf(key),
            PRODUCT_STOCK_KEY,
            amount.toString(),
            isIncrement.toString()
        ).awaitSingle()
    }

//    override suspend fun executeWithLock(productIds: List<Long>, runner: suspend () -> Unit) {
//
//        val txid = MDC.get(KEY_TXID).toLong()
//        logger.debug { ">> input  : $txid" }
//        val locks = productIds.sorted().map { redissonClient.getLock(REDISSON_KEY_PREFIX + PRODUCT_KEY + it) }
//
//        try {
//            locks.forEach {
//                check(it.tryLock(TRY_LOCK_TIME_OUT, LEASE_TIME, TimeUnit.SECONDS, txid).awaitSingle()) {
//                    throw AcquireLockTimeoutException()
//                }
//            }
//
//            transactionHelper.executeInNewTransaction(TARGET_METHOD_TIME_OUT) {
//                runner()
//            }
//        } catch (ex: Exception) {
//            when (ex) {
//                is TimeoutCancellationError -> throw TimeoutCancellationError()
//                is AcquireLockTimeoutException -> throw AcquireLockTimeoutException()
//                is NotEnoughStockException -> throw NotEnoughStockException()
//                else -> throw InternalServerError()
//            }
//        } finally {
//            withContext(NonCancellable) {
//                locks.forEach {
//                    logger.debug { ">> output  : $txid" }
//                    if (it.isHeldByThread(txid).awaitSingle()) {
//                        it.unlock(txid).awaitSingleOrNull()
//                    }
//                }
//            }
//        }
//    }

}