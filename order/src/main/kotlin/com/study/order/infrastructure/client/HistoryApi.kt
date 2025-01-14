package com.study.order.infrastructure.client

import com.study.order.application.client.HistoryApi
import com.study.order.application.dto.event.provider.OrderHistoryEvent
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitExchange

@Service
class HistoryApi(
    private val historyServiceWebClient: WebClient
) : HistoryApi {

    override suspend fun save(record: OrderHistoryEvent) {
        historyServiceWebClient.post()
            .uri("/history/order")
            .bodyValue(record)
            .awaitExchange { }
    }

}