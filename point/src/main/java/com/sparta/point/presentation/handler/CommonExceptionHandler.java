package com.sparta.point.presentation.handler;

import com.sparta.point.presentation.response.Response;
import com.sparta.point.application.exception.Error;
import com.sparta.point.application.exception.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"com.sparta.point.presentation.controller"})
public class CommonExceptionHandler {

    @ExceptionHandler(PointException.class)
    public Response<Void> ProductExceptionHandler(PointException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Response<Void> IllegalArgumentExceptionHandler(IllegalArgumentException e) {

        return Response.<Void>builder()
                .code(400)
                .message(e.getMessage())
                .build();
    }

}
