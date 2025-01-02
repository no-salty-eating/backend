package com.sparta.product.presentation.exception;

import org.springframework.http.HttpStatus;

public class NotFoundProductException extends CustomException{
    public NotFoundProductException() {
        super(Error.NOT_FOUND_PRODUCT, HttpStatus.NOT_FOUND);
    }
}
