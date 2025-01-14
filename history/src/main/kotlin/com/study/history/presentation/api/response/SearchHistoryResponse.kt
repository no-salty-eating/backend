package com.study.history.presentation.api.response

import com.study.history.domain.model.History

data class SearchHistoryResponse(
    val items: List<History>,
    val total: Long,
    val pageNext: List<Any>?,
)
