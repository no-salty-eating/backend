package com.study.order.application.dto.response

data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val stock: Int,
    val categoryList: List<CategoryResponse>,
)