package com.sparta.user.presentation.controller;

import com.sparta.user.application.dto.Response;
import com.sparta.user.application.dto.request.UpdateUserRequestDto;
import com.sparta.user.application.dto.response.LoginIdResponseDto;
import com.sparta.user.application.dto.response.UserResponseDto;
import com.sparta.user.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{loginId}")
    public Response<UserResponseDto> getUser(@RequestHeader(value = "X-UserId") String requestId,
                                             @RequestHeader(value = "X-Role") String requestRole,
                                             @PathVariable String loginId) {



        return userService.getUser(requestId, requestRole, loginId);
    }

    @PatchMapping("/{loginId}")
    public Response<LoginIdResponseDto> updateUser(
            @RequestHeader(value = "X-UserId") String requestId,
            @RequestHeader(value = "X-Role") String requestRole,
            @PathVariable String loginId,
            @Valid @RequestBody UpdateUserRequestDto requestDto) {

        return userService.updateUser(requestId, requestRole, loginId, requestDto);
    }

    @DeleteMapping("/{loginId}")
    public Response<LoginIdResponseDto> deleteUser(
            @RequestHeader(value = "X-UserId") String requestId,
            @RequestHeader(value = "X-Role") String requestRole,
            @PathVariable String loginId) {


        return userService.deleteUser(requestId, requestRole, loginId);
    }
}
