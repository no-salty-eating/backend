package com.sparta.product.application.exception.category;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;

public class AlreadyExistCategoryException extends CategoryException {
    public AlreadyExistCategoryException() {
        super(Error.ALREADY_EXIST_CATEGORY, HttpStatus.CONFLICT);
    }
}
