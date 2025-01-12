package com.sparta.coupon.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatusEnum {

    AVAILABLE, //사용가능
    USED, //사용됨
    EXPIRED//만료됨



}