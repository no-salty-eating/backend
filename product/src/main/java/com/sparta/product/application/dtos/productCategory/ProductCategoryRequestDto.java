package com.sparta.product.application.dtos.productCategory;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductCategoryRequestDto(Long categoryId) {
}
