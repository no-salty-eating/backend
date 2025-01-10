package com.study.order.application.client

import com.study.order.application.dto.response.ProductResponseDto

interface ProductService {

    suspend fun getProductList(productIdSet: Set<Long>): List<ProductResponseDto>

    suspend fun getProduct(productId: Long): ProductResponseDto?

}