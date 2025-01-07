package com.study.order.domain.repository

import com.study.order.domain.model.OrderDetail

interface OrderDetailRepository {

    suspend fun save(orderDetail: OrderDetail): OrderDetail
}