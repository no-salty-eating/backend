package com.sparta.product.application.exception.handler;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.exception.category.CategoryException;
import com.sparta.product.application.exception.common.Error;
import com.sparta.product.application.exception.common.ForbiddenException;
import com.sparta.product.application.exception.product.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"com.sparta.product.presentation.controller"})
public class CommonExceptionHandler {

    @ExceptionHandler(ProductException.class)
    public Response<Void> ProductExceptionHandler(ProductException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(CategoryException.class)
    public Response<Void> CategoryExceptionHandler(CategoryException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    public Response<Void> ForbiddenExceptionHandler(ForbiddenException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }
}

