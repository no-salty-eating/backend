package com.study.payment.infrastructure.client

import com.study.payment.application.client.PointService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class PointService (
    private val pointServiceWebClient: WebClient
): PointService{
}