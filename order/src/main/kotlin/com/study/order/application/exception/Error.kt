package com.study.order.application.exception

import org.springframework.http.HttpStatus

enum class Error(val status:HttpStatus, val message:String) {

    NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."),
    NOT_FOUND_ORDER_DETAIL(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."),

    INVALID_ORDER_STATUS(HttpStatus.NOT_ACCEPTABLE, "주문 상태가 올바르지 않습니다."),
}