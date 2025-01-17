package com.sparta.coupon.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coupon.application.service.UserCouponService;
import com.sparta.coupon.infrastructure.kafka.event.OrderSuccessEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
			OrderSuccessEvent orderSuccessEvent = objectMapper.readValue(replaceMessage, OrderSuccessEvent.class);
			userCouponService.useCoupon(orderSuccessEvent.userId(), orderSuccessEvent.userCouponId());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
