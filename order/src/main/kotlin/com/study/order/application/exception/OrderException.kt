package com.study.order.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class OrderException(val error: Error) : RuntimeException()

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundOrderException : OrderException(Error.NOT_FOUND_ORDER)

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundOrderDetailException : OrderException(Error.NOT_FOUND_ORDER_DETAIL)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidOrderStatusException : OrderException(Error.INVALID_ORDER_STATUS)