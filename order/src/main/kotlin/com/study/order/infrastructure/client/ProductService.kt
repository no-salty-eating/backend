package com.study.order.infrastructure.client

import com.study.order.application.client.ProductService
import com.study.order.application.dto.response.ProductResponseDto
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody


@Service
class ProductService(
    private val productServiceWebClient: WebClient
) : ProductService {

    override suspend fun getProductList(productIdSet: Set<Long>): List<ProductResponseDto> {
        return productServiceWebClient.get()
            .uri { builder ->
                builder.path("/products/list")
                    .queryParam("productIds", productIdSet.joinToString(","))
                    .build()
            }
            .retrieve()
            .awaitBody()
    }

    override suspend fun getProduct(productId: Long): ProductResponseDto? {
        return productServiceWebClient.get()
            .uri("/products/internal/$productId")
            .retrieve()
            .awaitBody()
    }

}