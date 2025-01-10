package com.study.order.application.dto.response

data class CouponResponse(
    val id: Long,
    val discountType: String,
    val discountValue: Int,
    val minOrderAmount: Int,
    val maxDiscountAmount: Int,
    val couponStatus: String,
)
