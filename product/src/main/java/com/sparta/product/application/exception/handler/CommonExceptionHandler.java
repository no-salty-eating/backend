package com.sparta.product.application.exception.handler;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.exception.category.CategoryException;
import com.sparta.product.application.exception.common.Error;
import com.sparta.product.application.exception.common.ForbiddenException;
import com.sparta.product.application.exception.product.ProductException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"com.sparta.product.presentation.controller"})
public class CommonExceptionHandler {

    @ExceptionHandler(ProductException.class)
    public Response<Void> ProductExceptionHandler(ProductException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(CategoryException.class)
    public Response<Void> CategoryExceptionHandler(CategoryException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(ForbiddenException.class)
    public Response<Void> ForbiddenExceptionHandler(ForbiddenException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> MethodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        String message = errors.toString().replace("{", "").replace("}", "");

        return Response.<Void>builder()
                .code(Error.METHOD_ARGUMENT_NOT_VALID.getCode())
                .message(message)
                .build();
    }
}

