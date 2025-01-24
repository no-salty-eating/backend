package com.sparta.product.application.exception.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenRoleException extends ForbiddenException{
    public ForbiddenRoleException(){
        super(Error.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

}
