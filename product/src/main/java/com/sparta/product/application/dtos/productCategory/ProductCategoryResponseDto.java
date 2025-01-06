package com.sparta.product.application.dtos.productCategory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.domain.core.ProductCategory;

public record ProductCategoryResponseDto(
        @JsonProperty("category_id") Long categoryId,
        @JsonProperty("category_name") String categoryName
) {
    public static ProductCategoryResponseDto from(ProductCategory productCategory) {
        return new ProductCategoryResponseDto(
                productCategory.getCategory().getId(),
                productCategory.getCategory().getName()
        );
    }
}
