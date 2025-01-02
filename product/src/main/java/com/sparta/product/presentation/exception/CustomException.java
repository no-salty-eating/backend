package com.sparta.product.presentation.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final Error error;
    private final HttpStatus httpStatus;

    public CustomException(Error error, HttpStatus httpStatus) {
        super(error.getMessage());
        this.error = error;
        this.httpStatus = httpStatus;
    }
}
