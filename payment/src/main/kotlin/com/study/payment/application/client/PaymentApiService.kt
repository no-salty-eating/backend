package com.study.payment.application.client

interface PaymentApiService {

    suspend fun retry(paymentId: Long)

}