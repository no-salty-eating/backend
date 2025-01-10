package com.study.payment.presentation.api.request

import com.study.payment.application.dto.request.PaySucceedRequestDto

data class PaySucceedRequest(
    val paymentKey: String,
    val orderId: String,
    val amount: Int,
    val paymentType: TossPaymentType,
)

fun PaySucceedRequest.toDto() = PaySucceedRequestDto(
    paymentKey = this.paymentKey,
    orderId = this.orderId,
    amount = this.amount,
    paymentType = this.paymentType.name,
)