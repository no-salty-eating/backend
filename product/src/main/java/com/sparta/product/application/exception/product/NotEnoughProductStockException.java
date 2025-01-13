package com.sparta.product.application.exception.product;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class NotEnoughProductStockException extends ProductException {
    public NotEnoughProductStockException() {
        super(Error.NOT_ENOUGH_PRODUCT_STOCK_EXCEPTION, HttpStatus.BAD_REQUEST);
    }
}
