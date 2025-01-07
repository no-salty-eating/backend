package com.study.order.infrastructure.client

import com.study.order.application.client.PointService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class PointService(
    private val pointServiceWebClient: WebClient
) : PointService {

    //TODO: 해당 형식으로 API 만들어달라 요청
    override suspend fun validateUserPoints(userId: Long, pointAmount: Int): Boolean {
        return pointServiceWebClient.get()
            .uri { builder ->
                builder.path("/points/$userId")
                    .queryParam("pointAmount", pointAmount)
                    .build()
            }
            .retrieve()
            .awaitBody()
    }
}