package com.study.order.infrastructure.messaging.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.order.application.messaging.MessageService
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service

@Service
class KafkaMessageProducer(
    private val mapper: ObjectMapper,
    private val template: ReactiveKafkaProducerTemplate<String, String>,
) : MessageService {

    override suspend fun send(topic: String, message: String) {
        template.send(topic, message).awaitSingle()
    }

    override suspend fun <T> sendEvent(topic: String, event: T) {
        send(topic, mapper.writeValueAsString(event))
    }

}