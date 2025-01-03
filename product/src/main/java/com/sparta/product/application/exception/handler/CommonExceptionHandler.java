package com.sparta.product.application.exception.handler;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.exception.Error;
import com.sparta.product.application.exception.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"좌표"})
public class CommonExceptionHandler {

    @ExceptionHandler(ProductException.class)
    public Response<Void> ProductExceptionHandler(ProductException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }
}

