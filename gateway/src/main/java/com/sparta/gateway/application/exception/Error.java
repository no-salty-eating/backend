package com.sparta.gateway.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true)
public enum Error {

    METHOD_ARGUMENT_NOT_VALID(10000, "유효하지 않은 값입니다."),
    FORBIDDEN(10001, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(10002, "서버 오류입니다."),
    CIRCUIT_BREAKER_OPEN(10003, "이용량 증가로 현재 서비스가 불가능합니다."),
    SERVER_TIMEOUT(10004, "응답 시간을 초과하였습니다."),

    NOT_FOUND_TOKEN(10005, "JWT 토큰을 찾을 수 없습니다."),
    INVALID_TOKEN_SIGNATURE(10006, "유효하지 않은 JWT 서명 입니다."),
    EXPIRED_JWT_TOKEN(10007, "만료된 JWT token 입니다."),
    UNSUPPORTED_JWT_TOKEN(10008, "지원되지 않는 JWT 토큰 입니다."),
    INVALID_JWT_TOKEN(10009, "잘못된 JWT 토큰 입니다.");


    Integer code;
    String message;

}
