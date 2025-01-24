package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotEnoughTimeSaleStockInDbException extends TimeSaleException {
    public NotEnoughTimeSaleStockInDbException() {
        super(Error.NOT_ENOUGH_TIMESALE_STOCK_IN_DB, HttpStatus.BAD_REQUEST);
    }
}
