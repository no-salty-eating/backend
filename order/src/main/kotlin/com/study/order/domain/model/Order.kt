package com.study.order.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("TB_ORDER")
class Order (
    @Id
    var id: Long = 0,
    var userId: Long,
    var totalPrice: Int,
    var status: Status = Status.ORDER_PROGRESS,
    var destinationAddr: String,
    var pointAmount: Int = 0,
) : BaseEntity() {
}