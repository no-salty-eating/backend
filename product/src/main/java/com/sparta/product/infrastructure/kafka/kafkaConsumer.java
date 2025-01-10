package com.sparta.product.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.product.application.service.ProductService;
import com.sparta.product.application.service.TimeSaleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class kafkaConsumer {

    private final ProductService productService;
    private final TimeSaleService timeSaleService;
    private final ObjectMapper objectMapper;

    private static final String REDIS_STOCK = "redis_stock";
    private static final String DB_STOCK = "db_stock";
    private static final String PRODUCT_SERVICE = "product-service";

    @KafkaListener(topics = REDIS_STOCK, groupId = PRODUCT_SERVICE)
    public void receiveRedisMessage(String serializedMessage) {
        log.info("receiveRedisMessage : {}", serializedMessage);
        productService.stockManagementInRedis(serializedMessage);
    }

    @KafkaListener(topics = DB_STOCK, groupId = PRODUCT_SERVICE)
    public void receiveDbStock(String serializedMessage) {
        log.info("receiveDbStock: {}", serializedMessage);
        productService.stockManagementInDb(serializedMessage);
    }
}
