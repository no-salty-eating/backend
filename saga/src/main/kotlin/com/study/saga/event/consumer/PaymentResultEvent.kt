package com.study.saga.event.consumer

data class PaymentResultEvent(
    val pgOrderId: String,
    val paymentPrice: Int,
    val pgStatus: String,
)
