package com.study.order.presentation.api.controller.docs

import com.study.order.presentation.api.request.ProductQuantityRequest
import com.study.order.presentation.api.response.Response
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "Order", description = "주문 API")
abstract class OrderControllerDocs {

    @Operation(summary = "주문 생성", description = "주문을 생성하는 API 입니다.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "주문 생성 성공",
                content = [Content(schema = Schema(implementation = Response::class))]
            )
        ]
    )
    @PostMapping("/orders/create")
    abstract suspend fun create(
        @RequestBody request: List<ProductQuantityRequest>,
        @RequestHeader(name = "X-Id") userId: String,
    ): Response<Long>

}