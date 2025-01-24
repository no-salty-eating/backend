package com.study.history.presentation.api.controller

import com.study.history.application.service.OrderHistoryService
import com.study.history.application.service.dto.request.RequestOrderSaveHistory
import com.study.history.domain.model.History
import com.study.history.presentation.api.request.SearchQueryRequest
import com.study.history.presentation.api.response.Response
import com.study.history.presentation.api.response.SearchHistoryResponse
import kotlinx.coroutines.flow.Flow
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/history/order")
class OrderHistoryController (
    private val orderHistoryService: OrderHistoryService,
){

    @PostMapping
    suspend fun save(@RequestBody request: RequestOrderSaveHistory) : Response<History> {
        return Response(
            HttpStatus.CREATED.value(),
            HttpStatus.CREATED.reasonPhrase,
            orderHistoryService.save(request)
        )
    }

    @GetMapping("/{orderId}")
    suspend fun get(@PathVariable orderId: Long) : Response<History> {
        return Response(
            HttpStatus.OK.value(),
            HttpStatus.OK.reasonPhrase,
            orderHistoryService.get(orderId)
        )
    }

    @GetMapping("/all")
    suspend fun getAll(): Flow<History> {
        return orderHistoryService.getAll()
    }

    @DeleteMapping("/{orderId}")
    suspend fun delete(@PathVariable orderId: Long) {
        orderHistoryService.delete(orderId)
    }

    @DeleteMapping("/all")
    suspend fun deleteAll() {
        orderHistoryService.deleteAll()
    }

    @GetMapping("/search")
    suspend fun search(request: SearchQueryRequest): Response<SearchHistoryResponse> {
        return Response(
            HttpStatus.OK.value(),
            HttpStatus.OK.reasonPhrase,
            orderHistoryService.search(request)
        )
    }
}