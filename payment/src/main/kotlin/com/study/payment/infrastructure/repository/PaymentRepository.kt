package com.study.payment.infrastructure.repository

import com.study.payment.domain.model.Payment
import com.study.payment.domain.repository.PaymentRepository
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PaymentRepository :CoroutineCrudRepository<Payment,Long>, PaymentRepository{


}