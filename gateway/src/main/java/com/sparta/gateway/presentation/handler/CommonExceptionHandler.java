package com.sparta.gateway.presentation.handler;


import com.sparta.gateway.application.dto.Response;
import com.sparta.gateway.application.exception.Error;
import com.sparta.gateway.application.exception.GatewayException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public Response<Void> GatewayExceptionHandler(GatewayException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

}
