package com.study.order.application.service

import com.study.order.application.client.PaymentService
import com.study.order.application.dto.event.consumer.PaymentProcessingEvent
import com.study.order.application.exception.NotFoundOrderException
import com.study.order.domain.model.Order
import com.study.order.domain.model.OrderStatus.PAYMENT_PROGRESS
import com.study.order.domain.repository.OrderRepository
import com.study.order.infrastructure.config.log.LoggerProvider
import io.netty.handler.timeout.TimeoutException
import kotlinx.coroutines.delay
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Service
class OrderTestService(
    private val paymentService: PaymentService,
    private val orderRepository: OrderRepository,
) {

    companion object {
        private val logger = LoggerProvider.logger
    }

    @Transactional
    suspend fun updateOrderStatus(request: PaymentProcessingEvent) {
        val order = getOrderByPgOrderId(request.pgOrderId)
        order.updateStatus(PAYMENT_PROGRESS)

        orderRepository.save(order)

        try {
            paymentService.keyInjection(request.id)
        } catch (exception: TimeoutException) {
            logger.debug { ">> 오류 : ${exception.message}" }
            delay(getDelay())
            paymentService.keyInjection(request.id)
        }
    }

    private fun getDelay(): Duration {
        val temp = (2.0).pow(Random(3).nextInt()).toInt() * 1000
        val delay = temp + (0..temp).random()
        return delay.milliseconds
    }

    private suspend fun getOrderByPgOrderId(pgOrderId: String): Order {
        return orderRepository.findByPgOrderId(pgOrderId) ?: throw NotFoundOrderException()
    }
}