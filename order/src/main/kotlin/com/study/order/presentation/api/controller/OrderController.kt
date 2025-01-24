package com.study.order.presentation.api.controller

import com.study.order.application.service.OrderService
import com.study.order.presentation.api.controller.docs.OrderControllerDocs
import com.study.order.presentation.api.request.ProductQuantityRequest
import com.study.order.presentation.api.request.toCreateOrderRequestDto
import com.study.order.presentation.api.response.Response
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) : OrderControllerDocs() {

    @PostMapping("/create")
    override suspend fun create(
        @RequestBody request: List<ProductQuantityRequest>,
        @RequestHeader(name = "X-Id") userId: String,
    ): Response<Long> {
        return Response(
            HttpStatus.CREATED.value(),
            HttpStatus.CREATED.reasonPhrase,
            orderService.create(request.toCreateOrderRequestDto(userId))
        )
    }

}