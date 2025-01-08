package com.study.order.application.exception

open class PointException(val error: Error) : RuntimeException()

class NotEnoughPointException : PointException(Error.NOT_ENOUGH_POINT)