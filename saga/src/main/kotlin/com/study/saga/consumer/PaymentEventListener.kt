package com.study.saga.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.saga.event.consumer.PaymentProcessingEvent
import com.study.saga.event.consumer.PaymentResultEvent
import com.study.saga.orchestrator.Orchestrator
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord

private const val PAYMENT_PROCESSING = "orchestrator:payment-processing"
private const val PAYMENT_RESULT = "orchestrator:payment-result"

class PaymentEventListener(
    private val mapper: ObjectMapper,
    private val orchestrator: Orchestrator,
    private val kafkaEventProcessor: KafkaEventProcessor,
) {

    @PostConstruct
    fun init() {
        kafkaEventProcessor.processEvent(PAYMENT_PROCESSING, "orchestrator") { record ->
            toPaymentProcessingEvent(record).let {
                orchestrator.processPaymentProcessing(it)
            }
        }

        kafkaEventProcessor.processEvent(PAYMENT_RESULT, "orchestrator") { record ->
            toPaymentResultEvent(record).let {
                orchestrator.processPaymentResult(it)
            }
        }
    }

    private fun toPaymentProcessingEvent(record: ConsumerRecord<String, String>): PaymentProcessingEvent {
        return mapper.readValue(record.value(), PaymentProcessingEvent::class.java)
    }

    private fun toPaymentResultEvent(record: ConsumerRecord<String, String>): PaymentResultEvent {
        return mapper.readValue(record.value(), PaymentResultEvent::class.java)
    }

}