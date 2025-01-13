package com.sparta.coupon.infrastructure.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UseCouponMessage(
        Long couponId
) {
}
