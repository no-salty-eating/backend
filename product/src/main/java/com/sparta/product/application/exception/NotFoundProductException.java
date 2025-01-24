package com.sparta.product.application.exception;

import org.springframework.http.HttpStatus;

public class NotFoundProductException extends ProductException{
    public NotFoundProductException() {
        super(Error.NOT_FOUND_PRODUCT, HttpStatus.NOT_FOUND);
    }
}
