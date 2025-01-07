package com.study.order.application.exception

import org.springframework.http.HttpStatus

enum class Error(val status:HttpStatus, val message:String) {

    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    NOT_FOUND_ORDER_DETAIL(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(HttpStatus.NOT_ACCEPTABLE, "주문 상태가 올바르지 않습니다."),

    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    NOT_ENOUGH_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다."),

    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "포인트 잔고가 부족합니다."),

    INVALID_COUPON_CATEGORY(HttpStatus.BAD_REQUEST, "해당 카테고리에 사용할 수 없는 쿠폰입니다."),
    INVALID_COUPON_PRICE(HttpStatus.BAD_REQUEST, "최소 주문 금액이 넘지 않아 쿠폰을 사용할 수 없습니다."),
    INVALID_COUPON(HttpStatus.NOT_ACCEPTABLE, "쿠폰 상태가 올바르지 않습니다."),
}