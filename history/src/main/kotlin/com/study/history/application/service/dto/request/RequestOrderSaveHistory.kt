package com.study.history.application.service.dto.request

import com.study.history.domain.model.History
import com.study.history.domain.model.OrderStatus
import java.time.LocalDateTime

data class RequestOrderSaveHistory(
    var orderId: Long,
    var userId: Long?,
    var description: String?,
    var orderStatus: OrderStatus?,
    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?,
) {
    fun toHistory(): History {
        return History(
            orderId = orderId,
            userId = userId ?: 0,
            description = description ?: "",
            orderStatus = orderStatus ?: OrderStatus.ORDER_PROGRESS,
            createdAt = createdAt ?: LocalDateTime.now(),
            updatedAt = updatedAt ?: LocalDateTime.now(),
        )
    }
}
