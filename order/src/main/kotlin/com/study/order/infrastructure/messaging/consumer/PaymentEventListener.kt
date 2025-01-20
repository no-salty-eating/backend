package com.study.order.infrastructure.messaging.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.order.application.dto.event.consumer.PaymentProcessingEvent
import com.study.order.application.dto.event.consumer.PaymentResultEvent
import com.study.order.application.service.OrderService
import com.study.order.application.service.OrderTestService
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Configuration


@Configuration
class PaymentEventListener(
    private val kafkaEventProcessor: KafkaEventProcessor,
    private val mapper: ObjectMapper,
    private val orderService: OrderService,
    private val orderTest: OrderTestService,
) {
    companion object {
        private const val PAYMENT_PROCESSING = "payment-processing"
        private const val PAYMENT_PROCESSING_TEST = "payment-processing-test"
        private const val PAYMENT_RESULT = "payment-result"
        private const val PAYMENT_RESULT_TEST = "payment-result-test"
    }

    @PostConstruct
    fun init() {

        kafkaEventProcessor.publish(PAYMENT_PROCESSING, "order-process") { record ->
            toPaymentProcessingEvent(record).let {
                orderService.updateOrderStatus(it)
            }
        }

        kafkaEventProcessor.publish(PAYMENT_RESULT, "order-process") { record ->
            toPaymentResultEvent(record).let {
                orderService.updateOrderStatus(it)
            }
        }

        kafkaEventProcessor.publish(PAYMENT_PROCESSING_TEST, "order-process-test") {record ->
            toPaymentProcessingEvent(record).let {
                orderTest.updateOrderStatus(it)
            }
        }

        kafkaEventProcessor.publish(PAYMENT_RESULT_TEST, "order-process-test") { record ->
            toPaymentResultEvent(record).let {
                orderService.updateOrderStatus(it)
            }
        }

    }

    private fun toPaymentProcessingEvent(record: ConsumerRecord<String, String>): PaymentProcessingEvent {
        return mapper.readValue(record.value(), PaymentProcessingEvent::class.java)
    }

    private fun toPaymentResultEvent(record: ConsumerRecord<String,String>): PaymentResultEvent {
        return mapper.readValue(record.value(), PaymentResultEvent::class.java)
    }

}