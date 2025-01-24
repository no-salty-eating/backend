package com.sparta.product.application.exception.category;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundCategoryException extends CategoryException {
    public NotFoundCategoryException() {
        super(Error.NOT_FOUND_CATEGORY, HttpStatus.NOT_FOUND);
    }
}
