package com.study.order.application.client

import com.study.order.application.dto.response.CouponResponse

interface CouponService {

    suspend fun getCouponList(userId: Long, couponIdSet: Set<Long>): List<CouponResponse>

}