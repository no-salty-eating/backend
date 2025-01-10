package com.study.order.application.dto.event.consumer

data class PaymentProcessingEvent(
    val id: Long,
    val pgOrderId: String,
)
