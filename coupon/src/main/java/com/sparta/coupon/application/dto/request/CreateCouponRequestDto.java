package com.sparta.coupon.application.dto.request;

import com.sparta.coupon.model.DiscountTypeEnum;
import com.sparta.coupon.model.core.Coupon;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCouponRequestDto {
    @NotBlank(message = "쿠폰 정책 이름은 필수입니다.")
    private String name;

    @NotNull(message = "할인 타입은 필수입니다.")
    private DiscountTypeEnum discountType;

    @NotNull(message = "할인 값은 필수입니다.")
    @Min(value = 1, message = "할인 값은 1 이상이어야 합니다.")
    private Integer discountValue;

    @NotNull(message = "최소 주문 금액은 필수입니다.")
    @Min(value = 0, message = "최소 주문 금액은 0 이상이어야 합니다.")
    private Integer minOrderAmount;

    @NotNull(message = "최대 할인 금액은 필수입니다.")
    @Min(value = 1, message = "최대 할인 금액은 1 이상이어야 합니다.")
    private Integer maxDiscountAmount;

    @NotNull(message = "총 수량은 필수입니다.")
    @Min(value = 1, message = "총 수량은 1 이상이어야 합니다.")
    private Integer totalQuantity;

    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalDateTime startTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    private LocalDateTime endTime;

    public Coupon toEntity() {
        return Coupon.createCoupon(
                name,
                discountType,
                discountValue,
                minOrderAmount,
                maxDiscountAmount,
                totalQuantity,
                startTime,
                endTime
        );
    }
}