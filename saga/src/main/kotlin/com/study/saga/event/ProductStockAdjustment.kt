package com.study.saga.event


data class ProductStockAdjustment(
    val productId: Long,
    val quantity: Int,
    val isDecrement: Boolean,
)
