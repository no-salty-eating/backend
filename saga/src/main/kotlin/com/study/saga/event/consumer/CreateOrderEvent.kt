package com.study.saga.event.consumer

import com.study.saga.event.provider.ProductStockAdjustment

data class CreateOrderEvent(
    val userId : Long,
    val description: String,
    val pgOrderId: String,
    val paymentPrice: Int,
    val productStockAdjustmentList: List<ProductStockAdjustment>,
)
