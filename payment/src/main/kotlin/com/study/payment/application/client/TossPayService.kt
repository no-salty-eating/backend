package com.study.payment.application.client

import com.study.payment.application.dto.request.PaySucceedRequestDto
import com.study.payment.infrastructure.client.response.PaymentResponse

interface TossPayService {

    suspend fun confirm(request: PaySucceedRequestDto): PaymentResponse

}