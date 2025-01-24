package com.study.payment.application.exception

enum class Error(val status: Int, val message: String) {

    INVALID_COUPON_CATEGORY(2200, "해당 카테고리에 사용할 수 없는 쿠폰입니다."),
    INVALID_COUPON_PRICE(2201, "최소 주문 금액이 넘지 않아 쿠폰을 사용할 수 없습니다."),
    INVALID_COUPON(2202, "쿠폰 상태가 올바르지 않습니다."),

    NOT_FOUND_ORDER(3000, "주문을 찾을 수 없습니다."),
    NOT_FOUND_ORDER_DETAIL(3001, "주문 정보를 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(3200, "주문 상태가 올바르지 않습니다."),

    NOT_FOUND_PAYMENT(3000, "결제 정보를 찾을 수 없습니다."),
    INVALID_PAYMENT_PRICE(3100,"주문 정보와 결제 금액이 다릅니다."),
    INVALID_PAYMENT_STATUS(3200, "결제 상태가 올바르지 않습니다."),
    TOO_MANY_PAYMENT_REQUEST(3201, "결제 시도 횟수를 초과했습니다."),

    NOT_FOUND_PRODUCT(5000, "상품을 찾을 수 없습니다."),
    NOT_ENOUGH_STOCK(5200, "재고가 부족합니다."),

}