package com.sparta.product.application.exception.productCategory;

import com.sparta.product.application.exception.common.Error;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundProductCategoryException extends ProductCategoryException {
    public NotFoundProductCategoryException() {
        super(Error.NOT_FOUND_PRODUCTCATEGORY, HttpStatus.NOT_FOUND);
    }
}
