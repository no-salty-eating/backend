package com.study.payment.domain.model

enum class PgStatus (
    val key: String,
    val description: String,
) {
    CREATE("CREATE", "결제 생성"),
    AUTH_SUCCESS("AUTH_SUCCESS", "토스 승인 성공"),
    AUTH_FAIL("AUTH_FAIL","토스 승인 실패"),
    AUTH_INVALID("AUTH_INVALID","데이터 간 금액이 다름"),
    CAPTURE_REQUEST("CAPTURE_REQUEST","PG 승인 요청중"),
    CAPTURE_RETRY("CAPTURE_RETRY","PG 승인 요청 재시도중"),
    CAPTURE_SUCCESS("CAPTURE_SUCCESS","PG 승인 완료"),
    CAPTURE_FAIL("CAPTURE_FAIL","PG 승인 실패"),
}