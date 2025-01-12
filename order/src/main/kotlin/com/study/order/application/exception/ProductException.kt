package com.study.order.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class ProductException(val error: Error) : RuntimeException()

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundProductException : ProductException(Error.NOT_FOUND_PRODUCT)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class NotEnoughStockException : ProductException(Error.NOT_ENOUGH_STOCK)