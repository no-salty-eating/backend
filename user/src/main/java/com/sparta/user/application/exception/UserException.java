package com.sparta.user.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {

    private final Error error;
    private final HttpStatus httpStatus;
}