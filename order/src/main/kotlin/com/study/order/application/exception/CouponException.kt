package com.study.order.application.exception

open class CouponException(val error: Error) : RuntimeException()

class InvalidCouponCategoryException : CouponException(Error.INVALID_COUPON_CATEGORY)

class InvalidCouponPriceException : CouponException(Error.INVALID_COUPON_PRICE)

class InvalidCouponException : CouponException(Error.INVALID_COUPON)