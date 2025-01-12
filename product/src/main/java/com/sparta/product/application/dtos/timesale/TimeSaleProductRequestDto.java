package com.sparta.product.application.dtos.timesale;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record TimeSaleProductRequestDto(
        @JsonProperty("product_id") @NotNull(message = "상품 id 입력은 필수입니다.") Long productId,
        @JsonProperty("discount_rate") @Positive(message = "양의 정수여야 합니다.") @Max(value = 100, message = "100 이하만 가능합니다.") @NotNull(message = "필수 값 입니다.") Integer discountRate,
        @Positive(message = "양의 정수여야 합니다.") @NotNull(message = "필수 값 입니다.") Integer stock,
        @JsonProperty("timesale_start_time") @FutureOrPresent(message = "시간은 현재 이후여야 합니다.") @NotNull(message = "필수 값 입니다.") LocalDateTime timeSaleStartTime,
        @JsonProperty("timesale_end_time") @Future(message = "시간은 현재 이후여야 합니다.") @NotNull(message = "필수 값 입니다.") LocalDateTime timeSaleEndTime
        ) {
}
