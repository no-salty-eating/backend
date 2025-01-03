package com.sparta.product.presentation.exception.handler;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.presentation.exception.Error;
import com.sparta.product.presentation.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"좌표"})
public class CommonExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public Response<Void> MemberExceptionHandler(CustomException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }
}

