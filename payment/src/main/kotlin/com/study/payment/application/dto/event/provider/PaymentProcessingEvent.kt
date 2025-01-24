package com.study.payment.application.dto.event.provider

data class PaymentProcessingEvent(
    val id: Long,
    val pgOrderId: String,
)
