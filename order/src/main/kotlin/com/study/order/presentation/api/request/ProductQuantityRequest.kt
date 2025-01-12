package com.study.order.presentation.api.request

import com.study.order.application.dto.request.ProductQuantityRequestDto

data class ProductQuantityRequest (
    val productId: Long,
    val quantity: Int,
    val couponId: Long?,
)

fun ProductQuantityRequest.toDto() = ProductQuantityRequestDto(
    productId = this.productId,
    quantity = this.quantity,
    userCouponId = this.couponId,
)