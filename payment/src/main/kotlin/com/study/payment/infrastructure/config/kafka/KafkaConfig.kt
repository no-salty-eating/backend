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

//const val topicPayment = "create-order"
//
//@Configuration
//class KafkaConfig(
//    private val admin: KafkaAdmin,
//) : InitializingBean {
//
//    @Bean
//    fun reactiveProducer(properties: KafkaProperties): ReactiveKafkaProducerTemplate<String, String> {
//        return properties.buildProducerProperties().let { prop ->
//            prop[ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG] = true
//            SenderOptions.create<String, String>(prop)
//        }.let { ReactiveKafkaProducerTemplate(it) }
//    }
//
//    override fun afterPropertiesSet() {
//        admin.createOrModifyTopics(
//            TopicBuilder.name(topicPayment).partitions(1).replicas(1).build(),
//        )
//    }
//
//}