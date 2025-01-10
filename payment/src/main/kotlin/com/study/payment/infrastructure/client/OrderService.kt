package com.study.payment.infrastructure.client

import com.study.payment.application.client.OrderService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class OrderService(
    private val orderServiceWebClient: WebClient
) : OrderService {
}