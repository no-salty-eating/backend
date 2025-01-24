package com.sparta.user.presentation.controller;

import com.sparta.user.application.dto.Response;
import com.sparta.user.application.dto.response.JwtTokenResponseDto;
import com.sparta.user.application.dto.request.LoginRequestDto;
import com.sparta.user.application.dto.request.JoinRequestDto;
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

    @PostMapping("/join")
    public Response<LoginIdResponseDto> join(@Valid @RequestBody JoinRequestDto requestDto) {

        return userService.join(requestDto);
    }

    @PostMapping("/login")
    Response<JwtTokenResponseDto> login(@RequestBody LoginRequestDto requestDto) {

        return authService.login(requestDto);
    }
}
