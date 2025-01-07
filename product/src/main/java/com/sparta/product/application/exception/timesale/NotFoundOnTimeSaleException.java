package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class NotFoundOnTimeSaleException extends TimeSaleException {
    public NotFoundOnTimeSaleException() {
        super(Error.NOT_FOUND_ON_TIMESALE_PRODUCT, HttpStatus.NOT_FOUND);
    }
}
