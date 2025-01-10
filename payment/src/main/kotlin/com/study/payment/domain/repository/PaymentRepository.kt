package com.study.payment.domain.repository

import com.study.payment.domain.model.Payment

interface PaymentRepository {

    suspend fun save(payment: Payment): Payment

    suspend fun findByPgOrderId(pgOrderId: String): Payment?

    suspend fun findById(id:Long): Payment?

    suspend fun findAllById(ids:List<Long>): List<Payment>
}
