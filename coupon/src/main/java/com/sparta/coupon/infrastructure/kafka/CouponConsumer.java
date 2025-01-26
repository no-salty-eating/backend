package com.sparta.coupon.infrastructure.kafka;

import static com.sparta.coupon.application.exception.Error.JSON_PROCESSING_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.application.service.UserCouponService;
import com.sparta.coupon.infrastructure.kafka.event.IssueCouponMessage;
import com.sparta.coupon.infrastructure.kafka.event.UseCouponMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponConsumer {
    private final UserCouponService userCouponService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "coupon-issue-requests", groupId = "coupon-service", containerFactory = "couponKafkaListenerContainerFactory")
    public void consumeCouponIssueRequest(IssueCouponMessage message, Acknowledgment acknowledgment) {
        try {
            log.info("Received coupon issue request: {}", message);
            userCouponService.issueUserCoupon(message);
            // 메시지 처리 성공 후 수동 커밋
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process coupon issue request: {}", e.getMessage(), e);
            // 예외가 발생해도 acknowledge를 호출하지 않으면 메시지가 재처리될 수 있음
        }

    }

    @KafkaListener(topics = "order-success", groupId = "coupon-service")
    public void consumeOrderSuccessRequest(String serializedMessage, Acknowledgment acknowledgment) {
        log.info("receiveRedisMessage : {}", serializedMessage);
        String replaceMessage = serializedMessage.replace("[", "").replace("]", "");
        try {
            UseCouponMessage useCoupon = objectMapper.readValue(replaceMessage, UseCouponMessage.class);
            userCouponService.useCoupon(useCoupon.userId(), useCoupon.userCouponId());
            // 메시지 처리 성공 시 커밋
            acknowledgment.acknowledge();
        } catch (JsonProcessingException e) {
            throw new CouponException(JSON_PROCESSING_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}