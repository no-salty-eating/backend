package com.study.history.presentation.api.handler

import com.study.history.application.exception.OrderException
import com.study.history.presentation.api.response.Response
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OrderException::class)
    fun handleOrderException(ex: OrderException): Response<Unit> {
        return Response(ex.error.status,ex.error.message)
    }

}