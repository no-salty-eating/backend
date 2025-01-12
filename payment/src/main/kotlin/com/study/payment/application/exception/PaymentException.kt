package com.study.payment.application.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

open class PaymentException(val error: Error) : RuntimeException()

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundPaymentException : PaymentException(Error.NOT_FOUND_PAYMENT)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidPaymentPriceException : PaymentException(Error.INVALID_PAYMENT_PRICE)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidPaymentStatusException : PaymentException(Error.INVALID_PAYMENT_STATUS)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class TooManyPaymentRequestException : PaymentException(Error.TOO_MANY_PAYMENT_REQUEST)