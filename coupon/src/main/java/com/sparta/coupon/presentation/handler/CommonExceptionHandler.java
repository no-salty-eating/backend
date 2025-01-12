package com.sparta.coupon.presentation.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.application.dto.Response;
import com.sparta.coupon.application.exception.Error;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"com.sparta.coupon.presentation.controller"})
public class CommonExceptionHandler  {

    @ExceptionHandler(CouponException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> CouponExceptionHandler(CouponException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Void> MethodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        String message = errors.toString().replace("{", "").replace("}", "");

        return Response.<Void>builder()
                .code(Error.METHOD_ARGUMENT_NOT_VALID.getCode())
                .message(message)
                .build();
    }

    @ExceptionHandler(JsonProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Response<Void> JsonProcessingExceptionHandler(JsonProcessingException e) {
        return Response.<Void>builder()
                .code(Error.JSON_PROCESSING_ERROR.getCode())
                .message("Error processing JSON: " + e.getMessage())
                .build();
    }
}
