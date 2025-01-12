package com.sparta.coupon.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.coupon.model.core.Coupon;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetCouponResponseDto(Long id, String name) {

    public static GetCouponResponseDto from(Coupon coupon) {
        return new GetCouponResponseDto(
                coupon.getId(),
                coupon.getName()
        );
    }
}
