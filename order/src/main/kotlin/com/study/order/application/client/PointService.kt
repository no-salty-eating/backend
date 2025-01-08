package com.study.order.application.client

interface PointService {

    suspend fun validateUserPoints(userId: Long, pointAmount: Int) : Boolean

}