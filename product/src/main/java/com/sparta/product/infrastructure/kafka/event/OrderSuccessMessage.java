package com.sparta.product.infrastructure.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderSuccessMessage(
		@JsonProperty("productId")
        Long productId,
		@JsonProperty("quantity")
        Integer quantity,
		@JsonProperty("userCouponId")
        Long userCouponId,
		@JsonProperty("userId")
        Long userId
) {
}
