package com.sparta.coupon.application.dto.response;

import com.sparta.coupon.model.core.Coupon;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class GetCouponResponseDto {
    private Long id;
    private String name;

    public static GetCouponResponseDto from(Coupon coupon) {
        return GetCouponResponseDto.builder()
                .id(coupon.getId())
                .name(coupon.getName())
                .build();
    }
}