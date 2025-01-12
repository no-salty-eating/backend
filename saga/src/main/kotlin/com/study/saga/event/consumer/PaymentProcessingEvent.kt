package com.study.saga.event.consumer

data class PaymentProcessingEvent(
    val id: Long,
    val pgOrderId: String,
)
