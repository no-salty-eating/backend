package com.sparta.product.application.exception.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true)
public enum Error {

    NOT_FOUND_MEMBER(1000, "존재하지 않는 사용자입니다."),
    ALREADY_EXIST_EMAIL(1001, "이미 존재하는 이메일입니다."),
    NOT_CORRECT_CERTIFICATION_NUMBER(1002, "인증번호가 틀렸습니다."),
    EXPIRED_CERTIFICATION_NUMBER(1003, "인증번호가 만료되었습니다."),
    INVALID_PASSWORD(1004, "비밀번호가 맞지 않습니다."),
    ACCOUNT_NOT_ENABLED(1005, "활성화 되지 않은 계정입니다."),
    NOT_FOUND_WISHLIST(1006, "해당 위시리스트를 찾을 수 없습니다."),
    QUANTITY_NOT_ENOUGH(1007, "수량은 1개 이상이어야 합니다."),

    NOT_FOUND_ORDER(3000, "해당 주문을 찾을 수 없습니다."),
    OUT_OF_STOCK(3001, "재고가 부족합니다."),
    ALREADY_SHIPPING(3002, "이미 배송 중인 상품입니다."),
    RETURN_PERIOD_PASSED(3003, "반품 기간이 지났습니다."),
    ORDER_BEEN_CANCELED(3004, "주문이 취소되었습니다."),
    BEFORE_PURCHASE_TIME(3005, "구매 가능 시간이 아닙니다."),

    NOT_FOUND_PRODUCT(5000, "해당 상품을 찾을 수 없습니다."),
    IS_NOT_SALE_PRODUCT(5001, "판매 중인 상품이 아닙니다."),

    NOT_FOUND_PRODUCTCATEGORY(5050, "상품이나 카테고리를 찾을 수 없습니다. 정상적인 상태인지 확인해주세요."),

    ALREADY_EXIST_CATEGORY(6000, "이미 존재하는 카테고리입니다."),
    NOT_FOUND_CATEGORY(6001, "존재하지 않는 카테고리입니다."),
    NOT_CORRECT_CATEGORY(6300, "정확한 카테고리를 입력해주세요."),

    NOT_FOUND_TIMESALE_PRODUCT(7000, "해당 타임세일 상품을 찾을 수 없습니다."),
    NOT_FOUND_ON_TIMESALE_PRODUCT(7001, "진행 중인 타임세일 상품이 아닙니다."),
    INVALID_TIMESALE_START_TIME(7100, "시작 시간은 현재 시간 이후여야 합니다."),
    INVALID_TIMESALE_END_TIME(7101, "종료 시간은 시작 시간 이후여야 합니다."),
    DUPLICATE_TIMESALE_PRODUCT(7102, "이미 진행 중이거나 예정된 타임세일이 있습니다."),
    TIMESALE_QUANTITY_EXCEED_PRODUCT_STOCK(7300, "상품의 보유 수량을 초과하였습니다."),
    EXCEED_TIMESALE_QUANTITY(7301, "남은 타임세일 재고 수량을 초과하였습니다."),
    TIMESALE_SCHEDULE_ERROR(7500, "타임세일 스케줄링 중 오류가 발생했습니다."),

    INTERNAL_SERVER_ERROR(9999, "서버 오류입니다."),
    CIRCUIT_BREAKER_OPEN(10000, "이용량 증가로 현재 서비스가 불가능합니다."),
    SERVER_TIMEOUT(10001, "응답 시간을 초과하였습니다."),

    METHOD_ARGUMENT_NOT_VALID(20000, "유효하지 않은 값입니다."),
    FORBIDDEN(20001, "접근 권한이 없습니다.");

    Integer code;
    String message;

}
