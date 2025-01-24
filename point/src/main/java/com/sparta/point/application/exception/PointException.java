package com.sparta.point.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PointException extends RuntimeException {

    private final Error error;
    private final HttpStatus httpStatus;

    public PointException(Error error, HttpStatus httpStatus) {
        this.error = error;
        this.httpStatus = httpStatus;
    }
}
