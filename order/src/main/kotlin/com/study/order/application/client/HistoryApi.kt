package com.study.order.application.client

import com.study.order.application.dto.event.provider.OrderHistoryEvent

interface HistoryApi {

    suspend fun save(record: OrderHistoryEvent)
}