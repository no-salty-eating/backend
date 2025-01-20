package com.study.payment.presentation.api.controller

import com.study.payment.application.service.PaymentTestService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payment")
class PaymentRestController(
    private val paymentTest: PaymentTestService,
) {

    @PatchMapping("/{paymentId}")
    suspend fun retryRequestPayment(@PathVariable paymentId: Long) {
        paymentTest.retryRequestPayment(paymentId)
    }

    @PatchMapping("/injection/{paymentId}")
    suspend fun paymentKeyInjection(@PathVariable paymentId: Long) {
        paymentTest.keyInjection(paymentId)
    }
}