package com.study.order.presentation.api.request

import com.study.order.application.dto.request.CreateOrderRequestDto
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

fun List<ProductQuantityRequest>.toCreateOrderRequestDto(userId : String) = CreateOrderRequestDto(
    userId = userId.toLong(),
    products = this.map { it.toDto() }
)