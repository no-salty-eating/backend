package com.sparta.coupon.infrastructure.kafka.event;

public record UseCouponMessage(
        Long couponId
) {
}
