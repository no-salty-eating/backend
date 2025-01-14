package com.study.history.presentation.api.request

import com.study.history.domain.model.OrderStatus

data class SearchQueryRequest(
    val orderId: List<Long>?,
    val userId: List<Long>?,
    val keyword: String?,
    val orderStatus: List<OrderStatus>?,
    val fromDate: String?,
    val toDate: String?,
    val pageSize: Int = 10,
    val pageNext: List<Long>? = null
)