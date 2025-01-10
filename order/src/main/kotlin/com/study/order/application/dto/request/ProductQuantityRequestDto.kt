package com.study.order.application.dto.request

data class ProductQuantityRequestDto(
    val productId: Long,
    val quantity: Int,
    val couponId: Long?,
)
