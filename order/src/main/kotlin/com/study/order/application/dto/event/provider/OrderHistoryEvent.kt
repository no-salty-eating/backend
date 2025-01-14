package com.study.order.application.dto.event.provider

import com.study.order.domain.model.Order
import com.study.order.domain.model.OrderStatus
import java.time.LocalDateTime

data class OrderHistoryEvent(
    var orderId : Long,
    var userId : Long,
    var description : String,
    var orderStatus: OrderStatus,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) {
    companion object {
        fun fromOrder(order: Order, description: String): OrderHistoryEvent {
            return OrderHistoryEvent(
                orderId = order.id,
                userId = order.userId,
                description = description,
                orderStatus = order.orderStatus,
                createdAt = order.createdAt,
                updatedAt = order.updatedAt,
            )
        }
    }
}
