package com.sparta.coupon.infrastructure.kafka.event;

public record OrderSuccessEvent(
		Long productId,
		Integer quantity,
		Long userCouponId,
		Long userId
) {

}
