package com.study.saga.event

data class PaymentResultEvent(
    val pgOrderId: String,
    val paymentPrice: Int,
    val pgStatus: String,
)
