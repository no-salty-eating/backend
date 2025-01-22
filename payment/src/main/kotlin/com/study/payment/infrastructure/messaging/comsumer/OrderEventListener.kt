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
    }

    @PostConstruct
    fun init() {

        kafkaEventProcessor.publish(CREATE_ORDER, "payment-process") { record ->
            toPayment(record).let {
                paymentService.createPaymentInfo(it)
            }
        }

        kafkaEventProcessor.publish(CREATE_ORDER, "payment-process-test") { record ->
            toPayment(record).let {
                paymentTest.createPaymentInfoTest(it)
            }
        }

    }

    private fun toPayment(record: ConsumerRecord<String, String>): CreateOrderEvent {
        return mapper.readValue(record.value(), CreateOrderEvent::class.java)
    }
}