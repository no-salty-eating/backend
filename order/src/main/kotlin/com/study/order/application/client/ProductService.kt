package com.study.order.application.client

import com.study.order.application.dto.response.ProductResponseDto

interface ProductService {

    suspend fun getProduct(productId: Long): ProductResponseDto?

}