package com.study.order.application.dto.event

data class CreateOrderEvent(
    val userId : Long,
    val description: String,
    val pgOrderId: String,
    val paymentPrice: Int,
)
