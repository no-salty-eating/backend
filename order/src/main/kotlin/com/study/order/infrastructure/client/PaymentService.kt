package com.study.order.infrastructure.client

import com.study.order.application.client.PaymentService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class PaymentService(
    private val paymentServiceWebClient: WebClient
) : PaymentService {

    override suspend fun keyInjection(paymentId: Long) {
        paymentServiceWebClient.patch()
            .uri("/payment/injection/$paymentId")
            .retrieve()
            .bodyToMono<String>()
            .subscribe()
    }

}