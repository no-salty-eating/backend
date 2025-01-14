package com.sparta.user.application.dto.response;

import com.sparta.user.domain.core.UserRoleEnum;
import com.sparta.user.domain.core.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class UserResponseDto {

    private String loginId;

    private String name;

    private String email;

    private UserRoleEnum role;


    public static UserResponseDto of(User user) {
        return UserResponseDto.builder()
                .loginId(user.getLoginId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
