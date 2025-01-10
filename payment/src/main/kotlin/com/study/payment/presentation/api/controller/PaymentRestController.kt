package com.study.payment.presentation.api.controller

import com.study.payment.application.service.PaymentService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/payment")
class PaymentRestController(
    private val paymentService: PaymentService,
) {
    @PatchMapping("/{paymentId}")
    suspend fun retryRequestPayment(@PathVariable paymentId: Long) {
        paymentService.retryRequestPayment(paymentId)
    }
}