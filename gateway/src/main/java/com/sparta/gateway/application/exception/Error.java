package com.sparta.gateway.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true)
public enum Error {

    NOT_FOUND_USER(1000, "존재하지 않는 사용자입니다."),
    ALREADY_EXIST_EMAIL(1001, "이미 존재하는 이메일입니다."),
    NOT_CORRECT_CERTIFICATION_NUMBER(1002, "인증번호가 틀렸습니다."),
    EXPIRED_CERTIFICATION_NUMBER(1003, "인증번호가 만료되었습니다."),
    INVALID_PASSWORD(1004, "비밀번호가 맞지 않습니다."),
    ACCOUNT_NOT_ENABLED(1005, "활성화 되지 않은 계정입니다."),
    ALREADY_EXIST_ID(1006, "이미 존재하는 ID 입니다."),

    METHOD_ARGUMENT_NOT_VALID(9997, "유효하지 않은 값입니다."),
    FORBIDDEN(9998, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(9999, "서버 오류입니다."),
    CIRCUIT_BREAKER_OPEN(10000, "이용량 증가로 현재 서비스가 불가능합니다."),
    SERVER_TIMEOUT(10001, "응답 시간을 초과하였습니다."),
    NOT_VALID_ROLE_ENUM(10002, "유효하지 않은 권한입니다."),
    INVALID_UPDATE_REQUEST(10003, "수정할 내용이 없습니다."),

    NOT_FOUND_TOKEN(11000, "JWT 토큰을 찾을 수 없습니다."),
    INVALID_TOKEN_SIGNATURE(11000, "유효하지 않은 JWT 서명 입니다."),
    EXPIRED_JWT_TOKEN(11001, "만료된 JWT token 입니다."),
    UNSUPPORTED_JWT_TOKEN(11002, "지원되지 않는 JWT 토큰 입니다."),
    INVALID_JWT_TOKEN(11003, "잘못된 JWT 토큰 입니다.");

    Integer code;
    String message;

}
