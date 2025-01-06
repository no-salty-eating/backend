package com.study.order.presentation.api.handler

import com.study.order.application.exception.OrderException
import com.study.order.presentation.api.response.Response
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OrderException::class)
    fun handleOrderException(ex: OrderException): Response<Unit> {
        return Response(ex.error.status.value(),ex.error.message)
    }

}