package com.sparta.coupon.infrastructure.kafka;

import static com.sparta.coupon.application.exception.Error.JSON_PROCESSING_ERROR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.application.service.UserCouponService;
import com.sparta.coupon.infrastructure.kafka.event.UseCouponMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class kafkaConsumer {

	private final UserCouponService userCouponService;
	private final ObjectMapper objectMapper;

    private static final String ORDER_SUCCESS = "order-success";
    private static final String ORDER_SERVICE = "order-service";


    @KafkaListener(topics = ORDER_SUCCESS, groupId = ORDER_SERVICE)
    public void receiveRedisMessage(String serializedMessage) {
        log.info("receiveRedisMessage : {}", serializedMessage);
        String replaceMessage = serializedMessage.replace("[", "").replace("]", "");
        try {
            UseCouponMessage useCoupon = objectMapper.readValue(replaceMessage, UseCouponMessage.class);
            userCouponService.useCoupon(useCoupon.userId(), useCoupon.userCouponId());
        } catch (JsonProcessingException e) {
            throw new CouponException(JSON_PROCESSING_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
