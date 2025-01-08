package com.study.payment.infrastructure.messaging.comsumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.study.payment.application.dto.event.consumer.CreateOrderEvent
import com.study.payment.application.service.PaymentService
import com.study.payment.infrastructure.config.kafka.TOPIC_PAYMENT
import com.study.payment.infrastructure.config.log.LoggerProvider
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import kotlin.math.log

@Configuration
class OrderConsumer (
    private val consumer: Consumer,
    private val mapper: ObjectMapper,
    private val paymentService: PaymentService,
) : InitializingBean {

    private val logger = LoggerProvider.logger

    override fun afterPropertiesSet() {
        logger.debug { ">> start consumer setting" }

        consumer.consume(TOPIC_PAYMENT, "payment-processing") {record ->
            logger.debug { ">> 메시지 읽기 시작함!" }
            toPayment(record).let {
                logger.debug { ">> DB 저장" }
                paymentService.createPaymentInfo(it)
            }
        }

        logger.debug { ">> ready consumer" }
    }

    private fun toPayment(record: ConsumerRecord<String, String>): CreateOrderEvent {
        return mapper.readValue(record.value(), CreateOrderEvent::class.java)
    }
}