package com.sparta.user.application.dto.response;


import com.sparta.user.domain.model.core.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginIdResponseDto {

    private String loginId;

    public static LoginIdResponseDto of(User user) {
        return LoginIdResponseDto.builder()
                .loginId(user.getLoginId())
                .build();
    }
}
