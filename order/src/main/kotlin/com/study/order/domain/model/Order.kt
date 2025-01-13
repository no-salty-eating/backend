package com.study.order.domain.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_ORDER")
class Order (
    @Id
    val id: Long = 0,
    val userId: Long,
    val pgOrderId: String? = null,
    val totalPrice: Int = 0,
    val paymentPrice: Int = 0,
    val pointAmount: Int = 0,
) : BaseEntity() {

    var orderStatus: OrderStatus = OrderStatus.ORDER_PROGRESS
        private set

    fun updateStatus(status: OrderStatus) {
        this.orderStatus = status
    }

    override fun equals(other: Any?): Boolean = kotlinEquals(
        other, arrayOf(
            Order::id
        )
    )

    override fun hashCode(): Int = kotlinHashCode(
        arrayOf(
            Order::id
        )
    )

    override fun toString(): String = kotlinToString(
        arrayOf(
            Order::id,
            Order::userId,
            Order::pgOrderId,
            Order::totalPrice,
            Order::paymentPrice,
            Order::pointAmount,
            Order::orderStatus,
        ), superToString = { super.toString() })
}