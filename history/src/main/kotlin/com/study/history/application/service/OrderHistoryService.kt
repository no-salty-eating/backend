package com.study.history.application.service

import com.study.history.application.exception.NotFoundOrderException
import com.study.history.application.service.dto.request.RequestOrderSaveHistory
import com.study.history.domain.model.History
import com.study.history.domain.repository.OrderHistoryRepository
import com.study.history.presentation.api.request.SearchQueryRequest
import com.study.history.presentation.api.response.SearchHistoryResponse
import kotlinx.coroutines.flow.Flow
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.stereotype.Service

@Service
class OrderHistoryService (
    private val orderHistoryRepository: OrderHistoryRepository,
    private val orderHistoryNativeService: OrderHistoryNativeService,
){

    suspend fun save(request: RequestOrderSaveHistory): History? {
        val data = orderHistoryRepository.findById(request.orderId)?.let { history ->
            request.userId?.let { history.userId = it }
            request.description?.let { history.description = it }
            request.orderStatus?.let { history.orderStatus = it }
            request.createdAt?.let { history.createdAt = it }
            request.updatedAt?.let { history.updatedAt = it }
            history
        } ?: request.toHistory()

        return orderHistoryRepository.save(data)
    }

    suspend fun get(orderId: Long): History {
        return orderHistoryRepository.findById(orderId) ?: throw NotFoundOrderException()
    }

    suspend fun getAll(): Flow<History> {
        return orderHistoryRepository.findAll()
    }

    suspend fun delete(orderId: Long) {
        orderHistoryRepository.deleteById(orderId)
    }

    suspend fun deleteAll() {
        orderHistoryRepository.deleteAll()
    }

    suspend fun search(request: SearchQueryRequest): SearchHistoryResponse? {
        return orderHistoryNativeService.search(request)
    }
}
