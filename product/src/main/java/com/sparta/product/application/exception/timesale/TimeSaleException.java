package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class TimeSaleException extends RuntimeException {
    private final Error error;
    private final HttpStatus httpStatus;
}
