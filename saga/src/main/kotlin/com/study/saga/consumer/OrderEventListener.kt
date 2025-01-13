package com.study.saga.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.study.saga.event.CreateOrderEvent
import com.study.saga.event.OrderSuccessEvent
import com.study.saga.orchestrator.Orchestrator
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Configuration

private const val CREATE_ORDER = "orchestrator:create-order"
private const val ORDER_SUCCESS = "orchestrator:order-success"

@Configuration
class OrderEventListener(
    private val mapper: ObjectMapper,
    private val orchestrator: Orchestrator,
    private val kafkaEventProcessor: KafkaEventProcessor,
) {

    @PostConstruct
    fun init() {
        kafkaEventProcessor.publish(CREATE_ORDER, "orchestrator") { record ->
            convertAsCreateOrderEvent(record).let {
                orchestrator.processOrderCreated(it)
            }
        }

        kafkaEventProcessor.publish(ORDER_SUCCESS, "orchestrator") { record ->
            convertAsOrderSuccessEvent(record).let {
                orchestrator.processOrderSuccess(it)
            }
        }
    }

    private fun convertAsCreateOrderEvent(record: ConsumerRecord<String, String>): CreateOrderEvent {
        return mapper.readValue(record.value(), CreateOrderEvent::class.java)
    }

    private fun convertAsOrderSuccessEvent(record : ConsumerRecord<String,String>): List<OrderSuccessEvent> {
        return mapper.readValue(record.value(), object : TypeReference<List<OrderSuccessEvent>>() {})
    }

}