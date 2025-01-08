package com.sparta.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JoinRequestDto {

    @NotBlank
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-z0-9]+$")
    private String loginId;

    @NotBlank
    @Size(min = 4, max = 10)
    @Pattern(regexp = "^[a-zA-Z\\d!@#$%^&*()_+\\-=]*$")
    private String password;

    @NotBlank
    @Size(min = 2, max = 10)
    @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "한글과 알파벳만 입력 가능합니다.")
    private String name;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotNull
    private String role;


}
