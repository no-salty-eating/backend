package com.sparta.product.application.exception.product;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundProductException extends ProductException{
    public NotFoundProductException() {
        super(Error.NOT_FOUND_PRODUCT, HttpStatus.NOT_FOUND);
    }
}
