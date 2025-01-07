package com.study.order.application.client

import com.study.order.application.dto.response.ProductResponse

interface ProductService {

    suspend fun getProductList(productIdSet: Set<Long>): List<ProductResponse>

}