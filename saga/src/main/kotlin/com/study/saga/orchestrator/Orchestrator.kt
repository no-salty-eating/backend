package com.study.saga.orchestrator

import com.study.saga.event.CreateOrderEvent
import com.study.saga.event.PaymentProcessingEvent
import com.study.saga.event.PaymentResultEvent
import com.study.saga.event.OrderSuccessEvent
import com.study.saga.event.ProductStockAdjustment
import com.study.saga.provider.KafkaEventPublisher
import org.springframework.stereotype.Service

private const val CREATE_ORDER = "create-order"
private const val ORDER_SUCCESS = "order-success"
private const val PRODUCT_STOCK_ADJUSTMENT = "product-stock-adjustment"
private const val PAYMENT_PROCESSING = "payment-processing"
private const val PAYMENT_RESULT = "payment-result"

@Service
class Orchestrator(
    private val eventPublisher: KafkaEventPublisher,
) {

    suspend fun processOrderCreated(event: CreateOrderEvent) {
        eventPublisher.sendEvent(CREATE_ORDER, event)
    }

    suspend fun processProductStockAdjustment(event: List<ProductStockAdjustment>) {
        eventPublisher.sendEvent(PRODUCT_STOCK_ADJUSTMENT, event)
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