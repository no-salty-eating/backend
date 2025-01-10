package com.study.payment.infrastructure.client

import com.study.payment.application.client.CouponService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class CouponService(
    private val couponServiceWebClient: WebClient
) : CouponService {


}