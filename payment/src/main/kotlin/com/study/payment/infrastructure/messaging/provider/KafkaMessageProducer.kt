package com.study.payment.infrastructure.messaging.provider

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.payment.application.messaging.MessageService
import kotlinx.coroutines.reactor.awaitSingle
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.sender.SenderOptions

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

@Configuration
class ReactiveKafkaInitializer {

    @Bean
    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, String> {
        return properties.buildProducerProperties().let { prop ->
            prop[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
            SenderOptions.create<String, String>(prop)
        }.let { option ->
            ReactiveKafkaProducerTemplate(option)
        }
    }
}