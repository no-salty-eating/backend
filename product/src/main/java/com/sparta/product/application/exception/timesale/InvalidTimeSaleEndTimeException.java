package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class InvalidTimeSaleEndTimeException extends TimeSaleException {
    public InvalidTimeSaleEndTimeException() {
        super(Error.INVALID_TIMESALE_END_TIME, HttpStatus.BAD_REQUEST);
    }
}
