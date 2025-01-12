package com.sparta.product.application.dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record ProductPurchaseRequestDto(
        @JsonProperty("id") @NotNull(message = "id 값은 필수입니다.") Long productId,
        @NotNull(message = "수량은 필수입니다.") Integer stock
) {
}
