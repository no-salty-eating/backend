package com.study.order.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductResponseDto(
    @JsonProperty("product_id")
    val productId: Long,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("price")
    val price: Int,
    @JsonProperty("stock")
    val stock: Int,
)