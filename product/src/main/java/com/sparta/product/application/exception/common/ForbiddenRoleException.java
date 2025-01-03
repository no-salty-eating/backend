package com.sparta.product.application.exception.common;

import org.springframework.http.HttpStatus;

public class ForbiddenRoleException extends ForbiddenException{
    public ForbiddenRoleException(){
        super(Error.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

}
