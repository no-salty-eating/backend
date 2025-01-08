package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class ExceedTimeSaleQuantityException extends TimeSaleException {
    public ExceedTimeSaleQuantityException() {
        super(Error.EXCEED_TIMESALE_QUANTITY, HttpStatus.BAD_REQUEST);
    }
}
