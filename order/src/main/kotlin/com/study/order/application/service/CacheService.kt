package com.study.order.application.service

import com.study.order.application.dto.response.ProductResponseDto

interface CacheService {

    suspend fun get(productId: Long): ProductResponseDto?

    suspend fun increment(productId: Long)

    suspend fun decrement(productId: Long)

    suspend fun getSize(productId: Long): Int?

}