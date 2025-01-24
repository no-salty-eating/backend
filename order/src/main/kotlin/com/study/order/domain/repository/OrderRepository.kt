package com.study.order.domain.repository

import com.study.order.domain.model.Order

interface OrderRepository {

    suspend fun save(order: Order): Order

    suspend fun findById(id: Long): Order?

    suspend fun findByPgOrderId(pgOrderId: String): Order?
}
