package com.study.payment.infrastructure.config.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions

@Configuration
class KafkaConfig(
    private val admin: KafkaAdmin,
) : InitializingBean {

    companion object {
        private const val PAYMENT_PROCESSING = "payment-processing"
        private const val PAYMENT_RESULT = "payment-result"
    }

    override fun afterPropertiesSet() {
        admin.createOrModifyTopics(
            TopicBuilder.name(PAYMENT_PROCESSING).partitions(1).replicas(1).build(),
            TopicBuilder.name(PAYMENT_RESULT).partitions(1).replicas(1).build(),
        )
    }

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