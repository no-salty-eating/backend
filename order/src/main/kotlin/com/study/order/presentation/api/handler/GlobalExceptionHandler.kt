package com.study.order.presentation.api.handler

import com.study.order.application.exception.CouponException
import com.study.order.application.exception.OrderException
import com.study.order.application.exception.ProductException
import com.study.order.presentation.api.response.Response
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OrderException::class)
    fun handleOrderException(ex: OrderException): Response<Unit> {
        return Response(ex.error.status,ex.error.message)
    }

    @ExceptionHandler(ProductException::class)
    fun handleProductException(ex: ProductException): Response<Unit> {
        return Response(ex.error.status,ex.error.message)
    }

    @ExceptionHandler(CouponException::class)
    fun handleCouponException(ex: CouponException): Response<Unit> {
        return Response(ex.error.status,ex.error.message)
    }

}