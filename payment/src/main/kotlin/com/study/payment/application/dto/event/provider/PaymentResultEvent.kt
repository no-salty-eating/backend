package com.study.payment.application.dto.event.provider

data class PaymentResultEvent(
    val pgOrderId: String?,
    val paymentPrice: Int,
    val pgStatus: String,
)
