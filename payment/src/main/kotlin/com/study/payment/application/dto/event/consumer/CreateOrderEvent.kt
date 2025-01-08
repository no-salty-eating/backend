package com.study.payment.application.dto.event.consumer

data class CreateOrderEvent(
    val userId: Long,
    val description: String,
    val pgOrderId: String,
    val paymentPrice: Int,
)
