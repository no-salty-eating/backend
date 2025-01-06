package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class TimeSaleQuantityExceedProductStockException extends TimeSaleException {
    public TimeSaleQuantityExceedProductStockException() {
        super(Error.TIMESALE_QUANTITY_EXCEED_PRODUCT_STOCK, HttpStatus.BAD_REQUEST);
    }
}
