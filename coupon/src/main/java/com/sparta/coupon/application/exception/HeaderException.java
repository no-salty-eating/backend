package com.sparta.coupon.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class HeaderException extends RuntimeException {

    private final Error error;
    private final HttpStatus httpStatus;
}