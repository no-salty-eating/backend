package com.study.order.application.service

import com.study.order.application.dto.response.ProductResponseDto

interface CacheService {

    suspend fun getProductInfo(productId: Long): ProductResponseDto?

    suspend fun decrementStock(productId: Long, amount: Int, isTimeSale: Boolean)

    suspend fun incrementStock(productId: Long, amount: Int, isTimeSale: Boolean)

//    suspend fun executeWithLock(productIds: List<Long>, runner: suspend () -> Unit)

    suspend fun saveOrderInfo(productId: Long, isTimeSale: Boolean)

    suspend fun isTimeSaleOrder(productId: Long): Boolean

    suspend fun deleteOrderInfo(productId: Long, isTimeSale: Boolean)
}