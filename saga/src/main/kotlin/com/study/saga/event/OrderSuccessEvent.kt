package com.study.saga.event

data class OrderSuccessEvent(
    val productId: Long,
    val quantity: Int,
    val couponId: Long?,
)
