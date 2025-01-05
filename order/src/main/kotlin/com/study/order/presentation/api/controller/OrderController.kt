package com.study.order.presentation.api.controller

import com.study.order.application.service.OrderService
import com.study.order.presentation.api.controller.docs.OrderControllerDocs
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController (
    private val orderService: OrderService,
) : OrderControllerDocs() {
}