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

    //TODO: 해당 형식으로 API 만들어달라 요청
    override suspend fun getCouponList(couponIdSet: Set<Long>): List<CouponResponse> {
        return couponServiceWebClient.get()
            .uri { builder ->
                builder.path("/coupons/list")
                    .queryParam("couponIds", couponIdSet.joinToString(","))
                    .build()
            }
            .retrieve()
            .awaitBody()
    }

}