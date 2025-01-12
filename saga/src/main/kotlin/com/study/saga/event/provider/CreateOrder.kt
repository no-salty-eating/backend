package com.study.saga.event.provider

data class CreateOrder(
    val userId: Long,
    val description: String,
    val pgOrderId: String,
    val paymentPrice: Int,
)