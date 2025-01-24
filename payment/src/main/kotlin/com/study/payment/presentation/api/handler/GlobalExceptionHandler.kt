package com.study.payment.presentation.api.handler

import com.study.payment.application.exception.PaymentException
import com.study.payment.presentation.api.response.Response
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PaymentException::class)
    fun handlePaymentException(ex: PaymentException): Response<Unit> {
        return Response(ex.error.status, ex.error.message)
    }

}