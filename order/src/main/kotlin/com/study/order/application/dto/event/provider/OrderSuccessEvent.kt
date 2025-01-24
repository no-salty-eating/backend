package com.study.order.application.dto.event.provider

data class OrderSuccessEvent(
    val productId: Long,
    val quantity: Int,
    val userCouponId: Long?,
    val userId: Long,
)
