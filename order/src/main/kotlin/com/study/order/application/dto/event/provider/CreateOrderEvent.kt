package com.study.order.application.dto.event.provider

data class CreateOrderEvent(
    val userId : Long,
    val description: String,
    val pgOrderId: String,
    val paymentPrice: Int,
)
