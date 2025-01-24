package com.study.payment.infrastructure.client

import com.study.payment.application.client.TossPayService
import com.study.payment.application.dto.request.PaySucceedRequestDto
import com.study.payment.infrastructure.client.response.PaymentResponse
import com.study.payment.infrastructure.config.webClient.WebClientConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.awaitBody

@Service
class TossPayApi(
    @Value("\${payment.toss.domain}")
    private val domain: String,
    @Value("\${payment.toss.key.secret}")
    private val secret: String,
    clientConfig: WebClientConfig,
) : TossPayService {

    private val client = clientConfig.createWebClient(domain, secret)

    override suspend fun confirm(request: PaySucceedRequestDto): PaymentResponse {
        return client.post().uri("/v1/payments/confirm")
            .header("Authorization", "Basic $secret")
            .bodyValue(request)
            .retrieve()
            .awaitBody()
    }
}