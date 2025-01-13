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

private const val TOPIC_PAYMENT = "create-order"

@Configuration
class KafkaConfig(
    private val admin: KafkaAdmin,
) : InitializingBean {

    //TODO: 오케스트레이터 완성되면 제거
    override fun afterPropertiesSet() {
        admin.createOrModifyTopics(
            TopicBuilder.name(TOPIC_PAYMENT).partitions(1).replicas(1).build(),
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