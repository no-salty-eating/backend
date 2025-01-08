package com.study.order.presentation.api.request

data class ProductQuantityRequest (
    val productId: Long,
    val quantity: Int,
    val couponId: Long?,
)