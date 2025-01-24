package com.sparta.product.application.exception.product;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundProductInRedisException extends ProductException {
    public NotFoundProductInRedisException() {
        super(Error.NOT_FOUND_PRODUCT_IN_REDIS, HttpStatus.NOT_FOUND);
    }
}
