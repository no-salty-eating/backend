package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class NotFoundTimeSaleException extends TimeSaleException {
    public NotFoundTimeSaleException() {
        super(Error.NOT_FOUND_TIMESALE_PRODUCT, HttpStatus.NOT_FOUND);
    }
}
