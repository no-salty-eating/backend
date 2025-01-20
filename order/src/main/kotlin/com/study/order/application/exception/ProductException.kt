package com.study.order.application.exception

open class ProductException(val error: Error) : RuntimeException()

class NotFoundProductException : ProductException(Error.NOT_FOUND_PRODUCT)

class NotEnoughStockException : ProductException(Error.NOT_ENOUGH_STOCK)