package com.sparta.coupon.application.dto.response;

import com.sparta.coupon.model.DiscountTypeEnum;
import com.sparta.coupon.model.core.Coupon;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class GetCouponDetailResponseDto {
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

    public static GetCouponDetailResponseDto from(Coupon coupon) {
        return GetCouponDetailResponseDto.builder()
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