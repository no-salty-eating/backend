package com.study.order.application.service

import com.study.order.domain.repository.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {

}
