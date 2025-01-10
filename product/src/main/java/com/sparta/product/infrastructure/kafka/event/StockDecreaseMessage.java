package com.sparta.product.infrastructure.kafka.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record StockDecreaseMessage(
        @JsonProperty("product_id") @NotNull(message = "상품 ID 입력은 필수입니다.") Long productId,
        @NotNull(message = "수량 입력은 필수입니다.") Integer stock,
        @NotNull(message = "필수 값 입니다.") Boolean isDecrease
) {
}
