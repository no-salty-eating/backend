package com.study.order.presentation.api.controller

import com.study.order.application.service.OrderService
import com.study.order.presentation.api.controller.docs.OrderControllerDocs
import com.study.order.presentation.api.request.CreateOrderRequest
import com.study.order.presentation.api.request.toDto
import com.study.order.presentation.api.response.Response
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController (
    private val orderService: OrderService,
) : OrderControllerDocs() {

    @PostMapping("/create")
    override suspend fun create(@RequestBody request: CreateOrderRequest): Response<Long> {
        return Response(
            HttpStatus.CREATED.value(),
            HttpStatus.CREATED.reasonPhrase,
            orderService.create(request.toDto())
        )
    }

}