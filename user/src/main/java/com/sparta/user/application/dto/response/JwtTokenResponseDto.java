package com.sparta.user.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtTokenResponseDto {
    private String accessToken;
}
