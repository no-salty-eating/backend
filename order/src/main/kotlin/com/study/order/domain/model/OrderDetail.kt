package com.study.order.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_ORDER_DETAIL")
class OrderDetail (
    @Id
    var id: Long = 0,
    var orderId: Long,
    var productId: Long,
    var couponId: Long,
    var price: Int,
    var amount: Int,
) : BaseEntity(){
}