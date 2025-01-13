package com.study.saga.config

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate
import reactor.kafka.sender.SenderOptions

private const val ORCHESTRATOR = "orchestrator:"
private const val CREATE_ORDER = "create-order"
private const val ORDER_SUCCESS = "order-success"
private const val PRODUCT_STOCK_ADJUSTMENT = "product-stock-adjustment"
private const val PAYMENT_PROCESSING = "payment-processing"
private const val PAYMENT_RESULT = "payment-result"

@Configuration
class KafkaConfig(
    private val admin: KafkaAdmin,
) : InitializingBean {

    override fun afterPropertiesSet() {
        admin.createOrModifyTopics(
            TopicBuilder.name(CREATE_ORDER).partitions(1).replicas(1).build(),
            TopicBuilder.name(ORDER_SUCCESS).partitions(1).replicas(1).build(),
            TopicBuilder.name(PRODUCT_STOCK_ADJUSTMENT).partitions(1).replicas(1).build(),
            TopicBuilder.name(PAYMENT_PROCESSING).partitions(1).replicas(1).build(),
            TopicBuilder.name(PAYMENT_RESULT).partitions(1).replicas(1).build(),
            TopicBuilder.name(ORCHESTRATOR + CREATE_ORDER).partitions(1).replicas(1).build(),
            TopicBuilder.name(ORCHESTRATOR + ORDER_SUCCESS).partitions(1).replicas(1).build(),
            TopicBuilder.name(ORCHESTRATOR + PRODUCT_STOCK_ADJUSTMENT).partitions(1).replicas(1).build(),
            TopicBuilder.name(ORCHESTRATOR + PAYMENT_PROCESSING).partitions(1).replicas(1).build(),
            TopicBuilder.name(ORCHESTRATOR + PAYMENT_RESULT).partitions(1).replicas(1).build(),
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