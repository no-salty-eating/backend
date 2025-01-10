package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TimeSaleQuantityExceedProductStockException extends TimeSaleException {
    public TimeSaleQuantityExceedProductStockException() {
        super(Error.TIMESALE_QUANTITY_EXCEED_PRODUCT_STOCK, HttpStatus.BAD_REQUEST);
    }
}
