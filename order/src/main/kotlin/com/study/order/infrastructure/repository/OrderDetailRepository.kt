package com.study.order.infrastructure.repository

import com.study.order.domain.model.OrderDetail
import com.study.order.domain.repository.OrderDetailRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface OrderDetailRepository: CoroutineCrudRepository<OrderDetail, Long> , OrderDetailRepository{
}