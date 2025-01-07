package com.sparta.user.application.service;


import static com.sparta.user.application.exception.Error.ALREADY_EXIST_EMAIL;
import static com.sparta.user.application.exception.Error.ALREADY_EXIST_ID;
import static com.sparta.user.application.exception.Error.INVALID_UPDATE_REQUEST;
import static com.sparta.user.application.exception.Error.NOT_FOUND_USER;
import static com.sparta.user.application.exception.Error.NOT_VALID_ROLE_ENUM;

import com.sparta.user.application.dto.Response;
import com.sparta.user.application.dto.request.JoinRequestDto;
import com.sparta.user.application.dto.request.UpdateUserRequestDto;
import com.sparta.user.application.dto.response.LoginIdResponseDto;
import com.sparta.user.application.dto.response.UserResponseDto;
import com.sparta.user.application.exception.UserException;
import com.sparta.user.domain.model.UserRoleEnum;
import com.sparta.user.domain.model.core.User;
import com.sparta.user.infrastructure.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "User Service")
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Response<LoginIdResponseDto> join(JoinRequestDto requestDto) {
        String loginId = requestDto.getLoginId();
        String password = passwordEncoder.encode(requestDto.getPassword());


        // 회원 중복 확인
        Optional<User> checkLoginId = userRepository.findByLoginId(loginId);
        if (checkLoginId.isPresent()) {
            throw new UserException(ALREADY_EXIST_ID, HttpStatus.CONFLICT);
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new UserException(ALREADY_EXIST_EMAIL, HttpStatus.CONFLICT);
        }

        // role 값이 UserRoleEnum에 존재하는지 확인
        String role = requestDto.getRole(); // SignupRequestDto에 role 값이 있다고 가정

        try {
            UserRoleEnum.valueOf(role); // Enum 값으로 변환 시도
        } catch (IllegalArgumentException e) {
            throw new UserException(NOT_VALID_ROLE_ENUM, HttpStatus.BAD_REQUEST);
        }
        // 사용자 등록
        User user = User.createUser(loginId, password, requestDto.getName(), email, role);
        userRepository.save(user);

        return new Response<>(HttpStatus.CREATED.value(), "회원가입 완료", LoginIdResponseDto.of(user));
    }

    @Transactional
    public Response<UserResponseDto> getUser(String requestId, String requestRole, String loginId) {
        // 요청 헤더의 role이 MASTER가 아닌 경우, 자신의 정보만 확인할 수 있음

        User user = validateUser(loginId);

        return new Response<>(HttpStatus.OK.value(), "조회 완료", UserResponseDto.of(user));
    }

    @Transactional
    public Response<LoginIdResponseDto> deleteUser(String requestId, String requestRole, String loginId) {

        User user = validateUser(loginId);

        user.softDelete();

        return new Response<>(HttpStatus.OK.value(), "삭제 완료", LoginIdResponseDto.of(user));
    }

    @Transactional
    public Response<LoginIdResponseDto> updateUser(String requestId, String requestRole, String loginId, UpdateUserRequestDto requestDto) {

        User user = validateUser(loginId);
        // 요청 DTO가 비어있는지 확인
        if (isRequestDtoInvalid(requestDto, user)) {
            throw new UserException(INVALID_UPDATE_REQUEST, HttpStatus.NOT_FOUND);
        }

        user.updateUser(passwordEncoder.encode(requestDto.getPassword()), requestDto.getName(), requestDto.getEmail(), requestDto.getIsPublic());

        return new Response<>(HttpStatus.OK.value(), "수정 완료", LoginIdResponseDto.of(user));
    }

    private User validateUser(String loginId) {
        return userRepository.findByLoginIdAndIsDeletedFalse(loginId)
                .orElseThrow(() -> new UserException(NOT_FOUND_USER, HttpStatus.NOT_FOUND));
    }

    private boolean isRequestDtoInvalid(UpdateUserRequestDto requestDto, User user) {
        return requestDto == null ||
                (requestDto.getPassword() == null || passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) &&
                        (requestDto.getName() == null || requestDto.getName().equals(user.getName())) &&
                        (requestDto.getEmail() == null || requestDto.getEmail().equals(user.getEmail())) &&
                        (requestDto.getIsPublic() == null || requestDto.getIsPublic().equals(user.isPublic()));
    }



}