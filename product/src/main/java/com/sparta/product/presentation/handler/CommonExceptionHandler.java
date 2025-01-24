package com.sparta.product.presentation.handler;

import com.sparta.product.application.exception.productCategory.ProductCategoryException;
import com.sparta.product.application.exception.timesale.TimeSaleException;
import com.sparta.product.application.exception.scheduler.ScheduleException;
import com.sparta.product.presentation.Response;
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

    // global error 경우가 있을 수 있으니 예외 처리가 이상하게 나오면 확인 필요
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

    @ExceptionHandler(ProductCategoryException.class)
    public Response<Void> ProductCategoryExceptionHandler(ProductCategoryException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(TimeSaleException.class)
    public Response<Void> TimeSaleExceptionHandler(TimeSaleException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(ScheduleException.class)
    public Response<Void> ScheduleExceptionHandler(ScheduleException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }
}

