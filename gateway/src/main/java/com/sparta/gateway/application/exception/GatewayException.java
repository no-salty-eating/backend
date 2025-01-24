package com.sparta.gateway.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class GatewayException extends RuntimeException {

    private final Error error;
    private final HttpStatus httpStatus;
}