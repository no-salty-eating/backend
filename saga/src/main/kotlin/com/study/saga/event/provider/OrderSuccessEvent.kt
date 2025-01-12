package com.study.saga.event.provider

data class OrderSuccessEvent(
    val productId: Long,
    val quantity: Int,
    val couponId: Long?,
)
