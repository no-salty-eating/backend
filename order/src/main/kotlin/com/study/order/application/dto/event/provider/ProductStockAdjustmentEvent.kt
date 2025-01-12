package com.study.order.application.dto.event.provider


data class ProductStockAdjustmentEvent(
    val productId: Long,
    val quantity: Int,
    val isDecrement: Boolean,
)
