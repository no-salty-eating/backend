package com.sparta.user.presentation.controller;

import com.sparta.user.application.dto.Response;
import com.sparta.user.application.dto.response.JwtTokenResponseDto;
import com.sparta.user.application.dto.request.LogInRequestDto;
import com.sparta.user.application.dto.request.SignInRequestDto;
import com.sparta.user.application.dto.response.LoginIdResponseDto;
import com.sparta.user.application.service.AuthService;
import com.sparta.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signIn")
    public Response<LoginIdResponseDto> signIn(@Valid @RequestBody SignInRequestDto requestDto) {

        return userService.signIn(requestDto);
    }

    @PostMapping("/logIn")
    Response<JwtTokenResponseDto> logIn(@RequestBody LogInRequestDto requestDto) {

        return authService.logIn(requestDto);
    }
}
