package com.study.order.application.dto.response

data class CouponResponse(
    val id: Long,
    val useCategoryId: Long,
    val discountPrice: Int?,
    val discountRate: Int?,
    val availablePrice: Int,
    val couponStatus: String,
)
