package com.study.payment.presentation.api.controller

import com.study.payment.application.service.PaymentService
import com.study.payment.presentation.api.request.PaySucceedRequest
import com.study.payment.presentation.api.request.toDto
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/payment")
class PaymentViewController(
    private val paymentService: PaymentService,
) {

    @GetMapping("/{pgOrderId}")
    suspend fun requestPaymentKey(@PathVariable pgOrderId: String, model: Model): String {
        model.addAttribute("payment", paymentService.getPaymentInfo(pgOrderId))
        return "pay.html"
    }

    @GetMapping("/success")
    suspend fun requestPayment(request: PaySucceedRequest): String {
        if (!paymentService.paymentKeyInjection(request.toDto()))
            return "pay-fail.html"
        return try {
            paymentService.requestPayment(request.toDto())
            "pay-success.html"
        } catch (e: Exception) {
            "pay-fail.html"
        }
    }
}