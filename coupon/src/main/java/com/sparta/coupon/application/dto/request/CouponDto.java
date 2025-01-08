package com.sparta.coupon.application.dto.request;

import com.sparta.coupon.model.DiscountTypeEnum;
import com.sparta.coupon.model.core.Coupon;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

public class CouponDto {

    @Getter
    @Builder
    public static class CreateRequest {
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

    @Getter
    @Builder
    public static class GetResponse {
        private Long id;
        private String name;

        public static GetResponse from(Coupon coupon) {
            return GetResponse.builder()
                    .id(coupon.getId())
                    .name(coupon.getName())
                    .build();
        }
    }


    @Getter
    @Builder
    public static class GetDetailResponse {
        private Long id;
        private String name;
        private DiscountTypeEnum discountType;
        private Integer discountValue;
        private Integer minOrderAmount;
        private Integer maxDiscountAmount;
        private Integer totalQuantity;
        private Integer issueQuantity;  // 발행된 수량
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime expireTime;  // 만료 시간 추가

        public static GetDetailResponse from(Coupon coupon) {
            return GetDetailResponse.builder()
                    .id(coupon.getId())
                    .name(coupon.getName())
                    .discountType(coupon.getDiscountType())
                    .discountValue(coupon.getDiscountValue())
                    .minOrderAmount(coupon.getMinOrderAmount())  // minOrderAmount 필드 사용
                    .maxDiscountAmount(coupon.getMaxDiscountAmount()) // maxDiscountAmount 필드 사용
                    .totalQuantity(coupon.getTotalQuantity())
                    .issueQuantity(coupon.getIssueQuantity())  // 발행된 수량
                    .startTime(coupon.getStartTime())
                    .endTime(coupon.getEndTime())
                    .createdAt(coupon.getCreatedAt())
                    .updatedAt(coupon.getUpdatedAt())
                    .expireTime(coupon.getExpireTime())  // expireTime 필드 사용
                    .build();
        }
    }
}