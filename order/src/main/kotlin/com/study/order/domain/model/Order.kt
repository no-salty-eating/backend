package com.study.order.domain.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_ORDER")
class Order (
    @Id
    var id: Long = 0,
    var userId: Long,
    var pgOrderId: String? = null,
    var totalPrice: Int = 0,
    var description: String? = null,
    var pointAmount: Int = 0,
    var orderStatus: OrderStatus = OrderStatus.ORDER_PROGRESS,
) : BaseEntity() {
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
            Order::description,
            Order::pointAmount,
            Order::orderStatus,
        ), superToString = { super.toString() })
}