package com.study.order.infrastructure.client

import com.study.order.application.client.ProductService
import com.study.order.application.dto.response.ProductResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody


@Service
class ProductService(
    private val productServiceWebClient: WebClient
) : ProductService {

    //TODO: 해당 형식으로 API 만들어달라 요청
    override suspend fun getProductList(productIdSet: Set<Long>): List<ProductResponse> {
        return productServiceWebClient.get()
            .uri { builder ->
                builder.path("/products/list")
                    .queryParam("productIds", productIdSet.joinToString(","))
                    .build()
            }
            .retrieve()
            .awaitBody()
    }

}