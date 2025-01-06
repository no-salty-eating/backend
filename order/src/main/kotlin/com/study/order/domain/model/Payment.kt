package com.study.order.domain.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

// 임시 생성
@Table("TB_PAYMENT")
class Payment (
    @Id
    var id: Long = 0,
    var userId: Long,
    var pgOrderId: String? = null,
    var pgKey: String? = null,
    var paymentPrice: Int = 0,
    var pgStatus: PgStatus = PgStatus.CREATE,
    var pgRetryCount: Int = 0,
) : BaseEntity(){
    override fun equals(other: Any?): Boolean = kotlinEquals(
        other, arrayOf(
            Payment::id
        )
    )

    override fun hashCode(): Int = kotlinHashCode(
        arrayOf(
            Payment::id
        )
    )

    override fun toString(): String = kotlinToString(
        arrayOf(
            Payment::id,
            Payment::userId,
            Payment::pgOrderId,
            Payment::pgKey,
            Payment::paymentPrice,
            Payment::pgStatus,
            Payment::pgRetryCount,
        ), superToString = { super.toString() })

    fun increaseRetryCount() {
        if (pgStatus == PgStatus.CAPTURE_RETRY) {
            pgRetryCount++
        }
    }
}