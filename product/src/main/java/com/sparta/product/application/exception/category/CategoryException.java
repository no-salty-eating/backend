package com.sparta.product.application.exception.category;

import com.sparta.product.application.exception.common.Error;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class CategoryException extends RuntimeException {

    private final Error error;
    private final HttpStatus httpStatus;
}
