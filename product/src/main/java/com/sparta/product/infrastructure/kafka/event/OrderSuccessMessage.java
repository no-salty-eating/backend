package com.sparta.product.infrastructure.kafka.event;

public record OrderSuccessMessage(
        Long productId,
        Integer quantity,
        Long userCouponId
) {
}
