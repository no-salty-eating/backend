package com.sparta.coupon.infrastructure.kafka.event;

public record UseCouponMessage(
		Long productId,
		Integer quantity,
		Long userCouponId,
		Long userId
) {

}
