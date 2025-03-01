package com.study.order.domain.model

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_ORDER_DETAIL")
class OrderDetail(
    var orderId: Long,
    var productId: Long,
    var userCouponId: Long? = null,
    var price: Int,
    var quantity: Int = 0,
    @Id
    val id: Long = 0,
) : BaseEntity() {
    override fun equals(other: Any?): Boolean = kotlinEquals(
        other, arrayOf(
            OrderDetail::orderId,
            OrderDetail::productId,
        )
    )

    override fun hashCode(): Int = kotlinHashCode(
        arrayOf(
            OrderDetail::orderId,
            OrderDetail::productId,
        )
    )

    override fun toString(): String = kotlinToString(
        arrayOf(
            OrderDetail::orderId,
            OrderDetail::productId,
            OrderDetail::userCouponId,
            OrderDetail::price,
            OrderDetail::quantity,
        ), superToString = { super.toString() })

}