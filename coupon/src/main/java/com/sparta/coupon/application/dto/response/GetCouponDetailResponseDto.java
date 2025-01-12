package com.sparta.coupon.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.coupon.model.DiscountTypeEnum;
import com.sparta.coupon.model.core.Coupon;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetCouponDetailResponseDto(
        Long id,
        String name,
        DiscountTypeEnum discountType,
        Integer discountValue,
        Integer minOrderAmount,
        Integer maxDiscountAmount,
        Integer totalQuantity,
        Integer issueQuantity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime expireTime
) {
    public static GetCouponDetailResponseDto from(Coupon coupon) {
        return new GetCouponDetailResponseDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderAmount(),
                coupon.getMaxDiscountAmount(),
                coupon.getTotalQuantity(),
                coupon.getIssueQuantity(),
                coupon.getStartTime(),
                coupon.getEndTime(),
                coupon.getCreatedAt(),
                coupon.getUpdatedAt(),
                coupon.getExpireTime()
        );
    }


}
