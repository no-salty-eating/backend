package com.study.payment.application.exception

open class PaymentException(val error: Error) : RuntimeException()

class NotFoundPaymentException : PaymentException(Error.NOT_FOUND_PAYMENT)

class InvalidPaymentPriceException : PaymentException(Error.INVALID_PAYMENT_PRICE)

class InvalidPaymentStatusException : PaymentException(Error.INVALID_PAYMENT_STATUS)

class TooManyPaymentRequestException : PaymentException(Error.TOO_MANY_PAYMENT_REQUEST)