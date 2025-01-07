package com.study.order.infrastructure.config.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions

@Configuration
class ReactiveKafkaInitializer {

    @Bean
    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String,String> {
        return properties.buildProducerProperties().let{ prop ->
            prop[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
            SenderOptions.create<String,String>(prop)
        }.let { ReactiveKafkaProducerTemplate(it) }
    }

}