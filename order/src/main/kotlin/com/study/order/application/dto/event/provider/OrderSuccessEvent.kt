package com.study.order.application.dto.event.provider

data class OrderSuccessEvent(
    val productId: Long,
    val quantity: Int,
    val couponId: Long?,
)
