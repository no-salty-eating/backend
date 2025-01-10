package com.study.payment.application.service

interface CacheService {

    suspend fun put(paymentId: Long)

    suspend fun remove(paymentId: Long)

    suspend fun getAll(): List<Long>
}