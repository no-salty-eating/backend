package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateTimeSaleException extends TimeSaleException {
    public DuplicateTimeSaleException() {
        super(Error.DUPLICATE_TIMESALE_PRODUCT, HttpStatus.BAD_REQUEST);
    }
}
