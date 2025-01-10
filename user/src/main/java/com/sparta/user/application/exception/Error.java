package com.sparta.user.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true)
public enum Error {

    // 조회 (GET) 오류 - 1000번대
    NOT_FOUND_USER(1000, "존재하지 않는 사용자입니다."), // 사용자 조회 시
    ACCOUNT_NOT_PUBLIC(1001, "활성화되지 않은 계정입니다."), // 계정 조회 시
    INVALID_PASSWORD(1002, "비밀번호가 맞지 않습니다."),

    // 추가 (POST) 오류 - 1100번대
    ALREADY_EXIST_EMAIL(1100, "이미 존재하는 이메일입니다."), // 이메일 추가 시
    ALREADY_EXIST_ID(1101, "이미 존재하는 ID 입니다."), // ID 추가 시


    METHOD_ARGUMENT_NOT_VALID(9997, "유효하지 않은 값입니다."),
    FORBIDDEN(9998, "접근 권한이 없습니다."),
    INTERNAL_SERVER_ERROR(9999, "서버 오류입니다."),
    CIRCUIT_BREAKER_OPEN(10000, "이용량 증가로 현재 서비스가 불가능합니다."),
    SERVER_TIMEOUT(10001, "응답 시간을 초과하였습니다."),
    NOT_VALID_ROLE_ENUM(10002, "유효하지 않은 권한입니다."),
    INVALID_UPDATE_REQUEST(10003, "수정할 내용이 없습니다.");

    Integer code;
    String message;

}
