package com.study.saga.event

data class PaymentProcessingEvent(
    val id: Long,
    val pgOrderId: String,
)
