package com.sparta.coupon.application.dto.request;

import com.sparta.coupon.domain.core.DiscountTypeEnum;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateCouponRequestDto(
        @NotBlank(message = "쿠폰 정책 이름은 필수입니다.")
        String name,

        @NotNull(message = "할인 타입은 필수입니다.")
        DiscountTypeEnum discountType,

        @NotNull(message = "할인 값은 필수입니다.")
        @Min(value = 1, message = "할인 값은 1 이상이어야 합니다.")
        Integer discountValue,

        @NotNull(message = "최소 주문 금액은 필수입니다.")
        @Min(value = 0, message = "최소 주문 금액은 0 이상이어야 합니다.")
        Integer minOrderAmount,

        @NotNull(message = "최대 할인 금액은 필수입니다.")
        @Min(value = 1, message = "최대 할인 금액은 1 이상이어야 합니다.")
        Integer maxDiscountAmount,

        @NotNull(message = "총 수량은 필수입니다.")
        @Min(value = 1, message = "총 수량은 1 이상이어야 합니다.")
        Integer totalQuantity,

        @NotNull(message = "시작 시간은 필수입니다.")
        @FutureOrPresent(message = "시작 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime startTime,

        @NotNull(message = "종료 시간은 필수입니다.")
        @FutureOrPresent(message = "시작 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime endTime
) {
}
