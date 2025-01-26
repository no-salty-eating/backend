package com.sparta.coupon.infrastructure.kafka.event;

import lombok.Builder;

@Builder
public record IssueCouponMessage(
		Long couponId,
		Long userId
) {

}
