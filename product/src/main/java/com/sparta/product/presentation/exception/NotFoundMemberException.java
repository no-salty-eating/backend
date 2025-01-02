package com.sparta.product.presentation.exception;

import org.springframework.http.HttpStatus;

public class NotFoundMemberException extends CustomException {
    public NotFoundMemberException() {
        super(Error.NOT_FOUND_MEMBER, HttpStatus.NOT_FOUND);
    }
}