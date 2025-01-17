package com.study.order.infrastructure.config.kafka

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
        private const val CREATE_ORDER = "create-order"
        private const val ORDER_SUCCESS = "order-success"
    }

    override fun afterPropertiesSet() {
        admin.createOrModifyTopics(
            TopicBuilder.name(CREATE_ORDER).partitions(1).replicas(1).build(),
            TopicBuilder.name(ORDER_SUCCESS).partitions(1).replicas(1).build(),
        )
    }

    @Bean
    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, String> {
        return properties.buildProducerProperties().let { prop ->
            prop[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
            SenderOptions.create<String, String>(prop)
        }.let { ReactiveKafkaProducerTemplate(it) }
    }

}