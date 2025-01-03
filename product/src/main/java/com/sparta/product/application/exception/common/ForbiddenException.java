package com.sparta.product.application.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ForbiddenException extends RuntimeException {
    private final Error error;
    private final HttpStatus httpStatus;
}
