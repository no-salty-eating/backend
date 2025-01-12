package com.study.saga.event.provider


data class ProductStockAdjustment(
    val productId: Long,
    val quantity: Int,
    val isDecrement: Boolean,
)
