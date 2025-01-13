package com.sparta.coupon.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.coupon.domain.core.Coupon;
import com.sparta.coupon.domain.core.UserCoupon;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetUserCouponDetailResponseDto (
        Long userCouponId,
        Long couponId,
        Long userId,
        String discountType,
        int discountValue,
        int minOrderAmount,
        int maxDiscountAmount,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        LocalDateTime usedAt) {

    public static GetUserCouponDetailResponseDto from(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();
        return new GetUserCouponDetailResponseDto(
                userCoupon.getId(),
                coupon.getId(),
                userCoupon.getUserId(),
                coupon.getDiscountType().name(),
                coupon.getDiscountValue(),
                coupon.getMinOrderAmount(),
                coupon.getMaxDiscountAmount(),
                coupon.getStartTime(),
                coupon.getEndTime(),
                userCoupon.getCouponStatus().name(),
                userCoupon.getUsedAt()
        );
    }
}