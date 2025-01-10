package com.study.order.application.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductData(
    @JsonProperty("product_id")
    val productId: String,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("price")
    val price: String,
    @JsonProperty("stock")
    val stock: String,
)
