package com.study.order.infrastructure.client

import com.study.order.application.client.CouponService
import com.study.order.application.dto.response.CouponResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class CouponService(
    private val couponServiceWebClient: WebClient
) : CouponService {

    override suspend fun getCouponList(userId: Long, couponIdSet: Set<Long>): List<CouponResponse> {
        return couponServiceWebClient.get()
            .uri { builder ->
                builder.path("/userCoupons/list/$userId")
                    .queryParam("userCouponIds", couponIdSet.joinToString(","))
                    .build()
            }
            .retrieve()
            .awaitBody()
    }

}