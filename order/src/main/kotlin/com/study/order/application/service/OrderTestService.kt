package com.study.order.application.service

import com.study.order.application.dto.event.consumer.PaymentProcessingEvent
import com.study.order.application.exception.NotFoundOrderException
import com.study.order.application.messaging.MessageService
import com.study.order.domain.model.Order
import com.study.order.domain.model.OrderStatus.PAYMENT_PROGRESS
import com.study.order.domain.repository.OrderRepository
import com.study.order.infrastructure.utils.TransactionHelper
import org.springframework.stereotype.Service

@Service
class OrderTestService(
    private val messageService: MessageService,
    private val orderRepository: OrderRepository,
    private val transactionHelper: TransactionHelper,
) {

    companion object {
        private const val KEY_INJECTION = "key-injection"
    }

    suspend fun updateOrderStatus(request: PaymentProcessingEvent) {
        val order = getOrderByPgOrderId(request.pgOrderId)
        order.updateStatus(PAYMENT_PROGRESS)

        transactionHelper.executeInNewTransaction {
            orderRepository.save(order)
        }

        messageService.sendEvent(KEY_INJECTION, request.id)
    }

    private suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?: throw NotFoundOrderException()
    }
}