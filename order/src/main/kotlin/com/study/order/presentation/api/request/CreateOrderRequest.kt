package com.study.order.presentation.api.request

data class CreateOrderRequest (
    val userId: Long,
    val pointAmount: Int = 0,
    val products: List<ProductQuantityRequest>,
)
