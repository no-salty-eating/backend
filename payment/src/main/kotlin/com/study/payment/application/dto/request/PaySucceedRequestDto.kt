package com.study.payment.application.dto.request

import com.study.payment.domain.model.Payment

data class PaySucceedRequestDto(
    val paymentKey: String,
    val orderId: String,
    val amount: Int,
    val paymentType: String,
) {
    companion object {
        fun from(payment: Payment): PaySucceedRequestDto {
            return PaySucceedRequestDto(
                paymentKey = payment.pgKey!!,
                orderId = payment.pgOrderId!!,
                amount = payment.paymentPrice,
                paymentType = payment.pgStatus.name,
            )
        }
    }
}