package com.study.payment.infrastructure.messaging.comsumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.payment.application.dto.event.consumer.CreateOrderEvent
import com.study.payment.application.service.PaymentService
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Configuration

private const val TOPIC_PAYMENT = "create-order"

@Configuration
class OrderEventConsumer(
    private val consumer: Consumer,
    private val mapper: ObjectMapper,
    private val paymentService: PaymentService,
) {

    @PostConstruct
    fun init() {

        consumer.consume(TOPIC_PAYMENT, "payment-process") { record ->
            toPayment(record).let {
                paymentService.createPaymentInfo(it)
            }
        }
    }

    private fun toPayment(record: ConsumerRecord<String, String>): CreateOrderEvent {
        return mapper.readValue(record.value(), CreateOrderEvent::class.java)
    }
}