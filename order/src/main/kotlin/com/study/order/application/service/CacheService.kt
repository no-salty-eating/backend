package com.study.order.application.service

import com.study.order.application.dto.response.ProductResponseDto

interface CacheService {

    suspend fun getProductInfo(productId: Long): ProductResponseDto?

    suspend fun increment(productId: Long, quantity: Int)

    suspend fun decrement(productId: Long, quantity: Int)

    suspend fun getSoldQuantity(productId: Long): Int?

}