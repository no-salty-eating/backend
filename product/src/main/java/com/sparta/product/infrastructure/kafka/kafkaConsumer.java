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
    private static final String REDIS_STOCK = "product-stock-adjustment";
    private static final String DB_STOCK = "order-success";
    private static final String PRODUCT_SERVICE = "product-service";

//    @KafkaListener(topics = REDIS_STOCK, groupId = PRODUCT_SERVICE)
//    public void receiveRedisMessage(String serializedMessage) {
//        log.info("receiveRedisMessage : {}", serializedMessage);
//        String replaceMessage = serializedMessage.replace("[", "").replace("]", "");
//        productService.stockManagementInRedis(replaceMessage);
//    }

    @KafkaListener(topics = DB_STOCK, groupId = PRODUCT_SERVICE)
    public void receiveDbStock(String serializedMessage) {
        log.info("receiveDbStock: {}", serializedMessage);
        String replaceMessage = serializedMessage.replace("[", "").replace("]", "");
        productService.stockManagementInDb(replaceMessage);
    }
}
