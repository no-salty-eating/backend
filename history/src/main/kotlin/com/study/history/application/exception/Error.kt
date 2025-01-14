package com.study.history.application.exception

enum class Error(val status: Int, val message: String) {

    INVALID_COUPON_CATEGORY(2200, "해당 카테고리에 사용할 수 없는 쿠폰입니다."),
    INVALID_COUPON_PRICE(2201, "최소 주문 금액이 넘지 않아 쿠폰을 사용할 수 없습니다."),
    INVALID_COUPON(2202, "쿠폰 상태가 올바르지 않습니다."),

    NOT_FOUND_ORDER(3000, "주문을 찾을 수 없습니다."),
    NOT_FOUND_ORDER_DETAIL(3001, "주문 정보를 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(3200, "주문 상태가 올바르지 않습니다."),

    NOT_FOUND_PAYMENT(3000, "결제 정보를 찾을 수 없습니다."),

    NOT_FOUND_PRODUCT(5000, "상품을 찾을 수 없습니다."),
    NOT_ENOUGH_STOCK(5200, "재고가 부족합니다."),

    ACQUIRE_LOCK_TIMEOUT(99999, "락 획득에 실패했습니다."),

    INTERNAL_SERVER_ERROR(100000, "서버 에러입니다.")
}