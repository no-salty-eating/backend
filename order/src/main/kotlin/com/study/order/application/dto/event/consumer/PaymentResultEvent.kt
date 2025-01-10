package com.study.order.application.dto.event.consumer

data class PaymentResultEvent(
    val pgOrderId: String,
    val paymentPrice: Int,
    val pgStatus: String,
)
