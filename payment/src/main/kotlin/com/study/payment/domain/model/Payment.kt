package com.study.payment.domain.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_PAYMENT")
class Payment (
    @Id
    val id: Long = 0,
    val userId: Long,
    val description: String? = null,
    val paymentPrice: Int = 0,
    val pgOrderId: String? = null,
) : BaseEntity(){

    var pgRetryCount: Int = 0
        private set

    var pgKey: String? = null
        private set

    var pgStatus: PgStatus = PgStatus.CREATE
        private set

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

    fun injectionPgKey(pgKey: String) {
        this.pgKey = pgKey
    }

    fun updateStatus(status: PgStatus) {
        this.pgStatus = status
    }

    fun increaseRetryCount() {
        if (pgStatus == PgStatus.CAPTURE_RETRY) {
            pgRetryCount++
        }
    }
}