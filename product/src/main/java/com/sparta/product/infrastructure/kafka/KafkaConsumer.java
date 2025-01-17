package com.sparta.product.infrastructure.kafka;

import com.sparta.product.application.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ProductService productService;
    private static final String ORDER_SUCCESS = "order-success";
    private static final String PRODUCT_SERVICE = "product-service";

    @KafkaListener(topics = ORDER_SUCCESS, groupId = PRODUCT_SERVICE)
    public void receiveDbStock(String serializedMessage) {
        log.info("receiveDbStock: {}", serializedMessage);
        String replaceMessage = serializedMessage.replace("[", "").replace("]", "");
        productService.stockManagementInDb(replaceMessage);
    }
}
