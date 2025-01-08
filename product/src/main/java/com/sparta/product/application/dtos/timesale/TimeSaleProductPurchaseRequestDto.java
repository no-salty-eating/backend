package com.sparta.product.application.dtos.timesale;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record TimeSaleProductPurchaseRequestDto(
        @JsonProperty("timesale_product_id") @NotNull(message = "상품 ID 입력은 필수입니다.") Long timeSaleProductId,
        @NotNull(message = "수량 입력은 필수입니다.") Integer quantity
) {
}
