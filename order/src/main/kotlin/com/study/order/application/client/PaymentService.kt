package com.study.order.application.client

interface PaymentService {

    suspend fun keyInjection(paymentId: Long)

}