package com.study.payment.infrastructure.config.kafka

import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin

private const val TOPIC_PAYMENT = "create-order"

@Configuration
class KafkaConfig(
    private val admin: KafkaAdmin,
) : InitializingBean {

    override fun afterPropertiesSet() {
        admin.createOrModifyTopics(
            TopicBuilder.name(TOPIC_PAYMENT).partitions(1).replicas(1).build(),
        )
    }

}