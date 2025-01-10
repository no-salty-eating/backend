package com.study.order.application.dto.request

data class CreateOrderRequestDto(
    val userId: Long,
    val products: List<ProductQuantityRequestDto>,
)
