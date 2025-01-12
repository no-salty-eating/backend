package com.study.order.domain.model

enum class OrderStatus (
    val key: String,
    val description: String,
) {

    ORDER_PROGRESS("ORDER_PROGRESS","주문 진행중"),
//    ORDER_COMPLETED("ORDER_COMPLETED","주문 완료"),
//    ORDER_CANCELED("ORDER_CANCELED","취소"),
    PAYMENT_PROGRESS("PAYMENT_PROGRESS", "결제 진행중"),
//    PAYMENT_COMPLETED("PAYMENT_COMPLETED","결제 완료"),
    PAYMENT_FAILED("PAYMENT_FAILED","결제 실패"),
    ORDER_FINALIZED("ORDER_FINALIZED","구매 확정");

}
