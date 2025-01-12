package com.study.saga.orchestrator

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.saga.event.consumer.CreateOrderEvent
import com.study.saga.event.consumer.PaymentProcessingEvent
import com.study.saga.event.consumer.PaymentResultEvent
import com.study.saga.event.provider.CreateOrder
import com.study.saga.event.provider.ProductStockAdjustment
import com.study.saga.event.provider.OrderSuccessEvent
import com.study.saga.provider.KafkaEventPublisher
import org.springframework.stereotype.Service

private const val CREATE_ORDER = "create-order"
private const val ORDER_SUCCESS = "order-success"
private const val PRODUCT_STOCK_ADJUSTMENT = "product-stock-adjustment"
private const val PAYMENT_PROCESSING = "payment-processing"
private const val PAYMENT_RESULT = "payment-result"

@Service
class Orchestrator(
    private val mapper: ObjectMapper,
    private val eventPublisher: KafkaEventPublisher,
) {

    suspend fun processOrderCreated(event: CreateOrderEvent) {
        val productStockAdjustmentEvent = event.productStockAdjustmentList.map {
            ProductStockAdjustment(
                it.productId,
                it.quantity,
                it.isDecrement
            )
        }

        val paymentEvent = CreateOrder(
            event.userId,
            event.description,
            event.pgOrderId,
            event.paymentPrice
        )

        eventPublisher.sendEvent(PRODUCT_STOCK_ADJUSTMENT, productStockAdjustmentEvent)
        eventPublisher.sendEvent(CREATE_ORDER, paymentEvent)
    }

    suspend fun processPaymentProcessing(event: PaymentProcessingEvent) {
        eventPublisher.sendEvent(PAYMENT_PROCESSING, event)
    }

    suspend fun processPaymentResult(event: PaymentResultEvent) {
        eventPublisher.sendEvent(PAYMENT_RESULT, event)
    }

    suspend fun processOrderSuccess(event: List<OrderSuccessEvent>) {
        eventPublisher.sendEvent(ORDER_SUCCESS, event)
    }
}