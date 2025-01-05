package com.study.order.domain.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

// 임시 생성
@Table("TB_PAYMENT")
class Payment (
    @Id
    var id: Long = 0,
    var orderId: Long,
    var userId: Long,
    var status: Status = Status.PAYMENT_PROGRESS,
    var paymentPrice: Int = 0,
    var approvalNumber: String? = null,
    var merchantNumber: String? = null,
    var pgProviderCode: String? = null,
) : BaseEntity(){
}