package com.study.payment.infrastructure.messaging.comsumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.payment.application.dto.event.consumer.CreateOrderEvent
import com.study.payment.application.service.PaymentService
import com.study.payment.infrastructure.config.log.LoggerProvider
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Configuration

@Configuration
class OrderConsumer(
    private val consumer: Consumer,
    private val mapper: ObjectMapper,
    private val paymentService: PaymentService,
) {
    companion object {
        const val TOPIC_PAYMENT = "create-order"
    }

    @PostConstruct
    fun init() {

        consumer.consume(TOPIC_PAYMENT, "payment-processing") { record ->
            toPayment(record).let {
                paymentService.createPaymentInfo(it)
            }
        }
    }

    private fun toPayment(record: ConsumerRecord<String, String>): CreateOrderEvent {
        return mapper.readValue(record.value(), CreateOrderEvent::class.java)
    }
}