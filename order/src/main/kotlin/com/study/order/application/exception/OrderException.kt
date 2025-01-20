package com.study.order.application.exception

open class OrderException(val error: Error) : RuntimeException()

class NotFoundOrderException : OrderException(Error.NOT_FOUND_ORDER)

class NotFoundOrderDetailException : OrderException(Error.NOT_FOUND_ORDER_DETAIL)

class InvalidOrderStatusException : OrderException(Error.INVALID_ORDER_STATUS)