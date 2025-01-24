package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTimeSaleStartTimeException extends TimeSaleException {
    public InvalidTimeSaleStartTimeException() {
        super(Error.INVALID_TIMESALE_START_TIME, HttpStatus.BAD_REQUEST);
    }
}
