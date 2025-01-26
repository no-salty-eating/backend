package com.sparta.coupon.infrastructure.kafka;

import com.sparta.coupon.infrastructure.kafka.event.IssueCouponMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponProducer {
    private static final String TOPIC = "coupon-issue-requests";
    private final KafkaTemplate<String, IssueCouponMessage> kafkaTemplate;

    public void sendCouponIssueRequest(IssueCouponMessage message) {
        kafkaTemplate.send(TOPIC, String.valueOf(message.couponId()), message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent message=[{}] with offset=[{}]", message, result.getRecordMetadata().offset());
                    } else {
                        log.error("Unable to send message=[{}] due to : {}", message, ex.getMessage());
                    }
                });
    }
}