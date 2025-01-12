package com.study.saga.consumer

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.study.saga.event.ProductStockAdjustment
import com.study.saga.orchestrator.Orchestrator
import jakarta.annotation.PostConstruct
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.context.annotation.Configuration

private const val PRODUCT_STOCK_ADJUSTMENT = "orchestrator:product-stock-adjustment"

@Configuration
class ProductEventListener (
    private val mapper: ObjectMapper,
    private val orchestrator: Orchestrator,
    private val kafkaEventProcessor: KafkaEventProcessor,
    ){

    @PostConstruct
    fun init() {
        kafkaEventProcessor.publish(PRODUCT_STOCK_ADJUSTMENT, "orchestrator") { record ->
            toProductStockAdjustmentEvent(record).let {
                orchestrator.processProductStockAdjustment(it)
            }
        }
    }

    private fun toProductStockAdjustmentEvent(record: ConsumerRecord<String, String>): List<ProductStockAdjustment> {
        return mapper.readValue(record.value(), object : TypeReference<List<ProductStockAdjustment>>() {})
    }
}