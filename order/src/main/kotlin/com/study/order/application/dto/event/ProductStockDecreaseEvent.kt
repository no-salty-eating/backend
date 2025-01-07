package com.study.order.application.dto.event

data class ProductStockDecreaseEvent(
    val productId: Long,
    val quantity: Int,
)
