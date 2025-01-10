package com.study.order.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class CouponException(val error: Error) : RuntimeException()

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidCouponCategoryException : CouponException(Error.INVALID_COUPON_CATEGORY)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidCouponPriceException : CouponException(Error.INVALID_COUPON_PRICE)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidCouponException : CouponException(Error.INVALID_COUPON)