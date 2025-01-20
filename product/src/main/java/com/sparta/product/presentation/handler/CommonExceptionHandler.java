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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"com.sparta.product.presentation.controller"})
public class CommonExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProductException.class)
    public Response<Void> productExceptionHandler(ProductException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CategoryException.class)
    public Response<Void> categoryExceptionHandler(CategoryException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Response<Void> forbiddenExceptionHandler(ForbiddenException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    // global error 경우가 있을 수 있으니 예외 처리가 이상하게 나오면 확인 필요
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> methodArgumentNotValidHandler(MethodArgumentNotValidException e) {
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ProductCategoryException.class)
    public Response<Void> productCategoryExceptionHandler(ProductCategoryException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TimeSaleException.class)
    public Response<Void> timeSaleExceptionHandler(TimeSaleException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ScheduleException.class)
    public Response<Void> scheduleExceptionHandler(ScheduleException e) {
        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }
}

