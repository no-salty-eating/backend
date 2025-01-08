package com.study.payment.infrastructure.messaging.comsumer

import kotlinx.coroutines.reactor.mono
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate
import org.springframework.stereotype.Service
import reactor.kafka.receiver.ReceiverOptions

@Service
class Consumer(
    private val properties: KafkaProperties,
) {

    fun consume(topic: String, groupId: String, runner: suspend (record: ConsumerRecord<String, String>) -> Unit) {
        properties.buildConsumerProperties().let { prop ->
            prop[ConsumerConfig.GROUP_ID_CONFIG] = groupId
            ReceiverOptions.create<String, String>(prop)
        }.subscription(listOf(topic)).let { option ->
            ReactiveKafkaConsumerTemplate(option)
        }.let { consumer ->
            consumer.receiveAutoAck().flatMap { record ->
                mono { runner.invoke(record) }
            }
        }.subscribe()
    }
}