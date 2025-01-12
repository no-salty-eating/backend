package com.study.saga.provider

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service

@Service
class KafkaEventPublisher(
    private val mapper: ObjectMapper,
    private val template: ReactiveKafkaProducerTemplate<String, String>,
) {
    suspend fun send(topic: String, message: String) {
        template.send(topic, message).awaitSingle()
    }

    suspend fun <T> sendEvent(topic: String, event: T) {
        send(topic, mapper.writeValueAsString(event))
    }
}