package com.study.order.presentation.api.request

import com.study.order.application.dto.request.CreateOrderRequestDto

data class CreateOrderRequest (
    val userId: Long,
    val products: List<ProductQuantityRequest>,
)

fun CreateOrderRequest.toDto() = CreateOrderRequestDto(
    userId = this.userId,
    products = this.products.map { it.toDto() },
)