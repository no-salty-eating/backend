package com.sparta.product.application.exception.product;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class NotFoundProductException extends ProductException{
    public NotFoundProductException() {
        super(Error.NOT_FOUND_PRODUCT, HttpStatus.NOT_FOUND);
    }
}
