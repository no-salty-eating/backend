package com.study.payment.domain.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

//TODO: 필드 접근자 재설정
@Table("TB_PAYMENT")
class Payment (
    @Id
    val id: Long = 0,
    var userId: Long,
    var description: String? = null,
    var paymentPrice: Int = 0,
    var pgOrderId: String? = null,
    var pgKey: String? = null,
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
            Payment::description,
            Payment::paymentPrice,
            Payment::pgOrderId,
            Payment::pgKey,
            Payment::pgStatus,
            Payment::pgRetryCount,
        ), superToString = { super.toString() })

    fun increaseRetryCount() {
        if (pgStatus == PgStatus.CAPTURE_RETRY) {
            pgRetryCount++
        }
    }
}