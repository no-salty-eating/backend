package com.sparta.coupon.domain.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountTypeEnum {

    RATE, //할인율
    AMOUNT//할인금액

}