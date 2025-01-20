package com.study.order.application.service

import com.study.order.application.client.HistoryApi
import com.study.order.application.client.PaymentService
import com.study.order.application.dto.event.consumer.PaymentProcessingEvent
import com.study.order.application.exception.NotFoundOrderException
import com.study.order.application.messaging.MessageService
import com.study.order.domain.model.Order
import com.study.order.domain.model.OrderStatus.PAYMENT_PROGRESS
import com.study.order.domain.repository.OrderDetailRepository
import com.study.order.domain.repository.OrderRepository
import com.study.order.infrastructure.config.log.LoggerProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderTestService(
    private val paymentService: PaymentService,
    private val orderRepository: OrderRepository,
) {

    @Transactional
    suspend fun updateOrderStatus(request: PaymentProcessingEvent) {
        val order = getOrderByPgOrderId(request.pgOrderId)
        order.updateStatus(PAYMENT_PROGRESS)

        orderRepository.save(order)
        paymentService.keyInjection(request.id)
    }

    private suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?: throw NotFoundOrderException()
    }
}