package com.study.payment.infrastructure.client

import com.study.payment.application.client.PaymentApiService
import com.study.payment.infrastructure.config.webClient.WebClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class PaymentApi(
    @Value("\${payment.self.domain}")
    domain: String,
    @Value("\${spring.application.name}")
    name: String,
    clientConfig: WebClientConfig,
) : PaymentApiService {

    private val client = clientConfig.createWebClient(domain, name)

    override suspend fun retry(paymentId: Long) {
        client.patch().uri("/payment/$paymentId")
            .retrieve()
            .bodyToMono<String>()
            .subscribe()
    }
}