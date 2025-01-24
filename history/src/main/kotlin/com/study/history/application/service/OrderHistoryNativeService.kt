package com.study.history.application.service

import com.study.history.presentation.api.request.SearchQueryRequest
import com.study.history.presentation.api.response.SearchHistoryResponse

interface OrderHistoryNativeService {

    suspend fun search(request: SearchQueryRequest): SearchHistoryResponse?
}