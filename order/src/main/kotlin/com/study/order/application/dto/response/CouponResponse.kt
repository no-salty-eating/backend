package com.study.order.application.dto.response

data class CouponResponse(
    val userCouponId: Long,
    val discountType: String,
    val discountValue: Int,
    val minOrderAmount: Int,
    val maxDiscountAmount: Int,
    val status: String,
)
