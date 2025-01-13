package com.sparta.product.infrastructure.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StockDecreaseMessage(
        Long productId,
        Integer quantity,
        @JsonProperty("decrement") Boolean isDecrease
) {
}
