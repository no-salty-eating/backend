package com.sparta.product.application.exception.timesale;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundTimeSaleInRedisException extends TimeSaleException {
    public NotFoundTimeSaleInRedisException() {
        super(Error.NOT_FOUND_TIMESALE_IN_REDIS, HttpStatus.NOT_FOUND);
    }
}
