package com.sparta.user.presentation.handler;

import com.sparta.user.application.dto.Response;
import com.sparta.user.application.exception.Error;
import com.sparta.user.application.exception.UserException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = {"com.sparta.user.presentation.controller"})
public class CommonExceptionHandler  {

    @ExceptionHandler(UserException.class)
    public Response<Void> UserExceptionHandler(UserException e) {

        Error error = e.getError();

        return Response.<Void>builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Void> MethodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        String message = errors.toString().replace("{", "").replace("}", "");

        return Response.<Void>builder()
                .code(Error.METHOD_ARGUMENT_NOT_VALID.getCode())
                .message(message)
                .build();
    }
}
