package com.sparta.coupon.application.dto.response;

import com.sparta.coupon.model.CouponStatusEnum;
import com.sparta.coupon.model.DiscountTypeEnum;
import com.sparta.coupon.model.core.Coupon;
import com.sparta.coupon.model.core.UserCoupon;
import java.time.LocalDateTime;

public record GetUserCouponDetailResponseDto (
        Long id,
        Long couponId,
        Long userId,
        DiscountTypeEnum discountType,
        int discountValue,
        int minOrderAmount,
        int maxDiscountAmount,
        LocalDateTime startTime,
        LocalDateTime endTime,
        CouponStatusEnum status,
        LocalDateTime usedAt) {

    public static GetUserCouponDetailResponseDto from(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();
        return new GetUserCouponDetailResponseDto(
                userCoupon.getId(),
                coupon.getId(),
                userCoupon.getUserId(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderAmount(),
                coupon.getMaxDiscountAmount(),
                coupon.getStartTime(),
                coupon.getEndTime(),
                userCoupon.getCouponStatus(),
                userCoupon.getUpdatedAt()
        );
    }
}