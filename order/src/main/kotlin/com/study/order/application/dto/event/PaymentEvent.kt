package com.study.order.application.dto.event

data class PaymentEvent(
    val userId : Long,
    val pgOrderId: String,
    val paymentPrice: Int,
)
