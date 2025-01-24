package com.study.order.infrastructure.repository

import com.study.order.domain.model.Order
import com.study.order.domain.repository.OrderRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: CoroutineCrudRepository<Order, Long>, OrderRepository {
}