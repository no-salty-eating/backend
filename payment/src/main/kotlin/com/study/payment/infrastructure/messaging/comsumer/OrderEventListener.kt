package com.study.payment.infrastructure.messaging.comsumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.payment.application.dto.event.consumer.CreateOrderEvent
import com.study.payment.application.service.PaymentService
import com.study.payment.application.service.PaymentTestService
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Configuration

@Configuration
class OrderEventListener(
    private val kafkaEventProcessor: KafkaEventProcessor,
    private val mapper: ObjectMapper,
    private val paymentService: PaymentService,
    private val paymentTest: PaymentTestService,
) {

    companion object {
        private const val CREATE_ORDER = "create-order"
        private const val CREATE_ORDER_TEST = "create-order-test"
        private const val KEY_INJECTION = "key-injection"
    }

    @PostConstruct
    fun init() {

        kafkaEventProcessor.publish(CREATE_ORDER, "payment-process") { record ->
            toPayment(record).let {
                paymentService.createPaymentInfo(it)
            }
        }

        kafkaEventProcessor.publish(CREATE_ORDER_TEST, "payment-process") { record ->
            toPayment(record).let {
                paymentTest.createPaymentInfoTest(it)
            }
        }

        kafkaEventProcessor.publish(KEY_INJECTION, "payment-process") {record ->
            paymentTest.keyInjection(record.value().toLong())
        }
    }

    private fun toPayment(record: ConsumerRecord<String, String>): CreateOrderEvent {
        return mapper.readValue(record.value(), CreateOrderEvent::class.java)
    }
}