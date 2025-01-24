package com.sparta.user.application.service;

import static com.sparta.user.application.exception.Error.ACCOUNT_NOT_PUBLIC;
import static com.sparta.user.application.exception.Error.INVALID_PASSWORD;
import static com.sparta.user.application.exception.Error.NOT_FOUND_USER;

import com.sparta.user.application.dto.Response;
import com.sparta.user.application.dto.response.JwtTokenResponseDto;
import com.sparta.user.application.dto.request.LoginRequestDto;
import com.sparta.user.application.exception.UserException;
import com.sparta.user.domain.core.User;
import com.sparta.user.domain.repository.UserRepository;
import com.sparta.user.infrastructure.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public Response<JwtTokenResponseDto> login(LoginRequestDto requestDto) {
        String loginId = requestDto.getLoginId();
        String password = requestDto.getPassword();

        // 사용자 확인
        User user = userRepository.findByLoginId(loginId).orElseThrow(
                () -> new UserException(NOT_FOUND_USER, HttpStatus.NOT_FOUND)
        );

        if (!user.isPublic()){
            throw new UserException(ACCOUNT_NOT_PUBLIC, HttpStatus.FORBIDDEN);
        }

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UserException(INVALID_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        JwtTokenResponseDto token = JwtTokenResponseDto.builder()
                .accessToken(jwtUtil.createToken(user.getLoginId(), user.getId() ,user.getRole()))
                .build();

        return new Response<>(HttpStatus.OK.value(), "로그인 성공", token);

    }
}
