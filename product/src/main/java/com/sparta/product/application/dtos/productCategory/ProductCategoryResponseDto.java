package com.sparta.product.application.dtos.productCategory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.domain.core.Category;

public record ProductCategoryResponseDto(
        @JsonProperty("category_id") Long categoryId,
        @JsonProperty("category_name") String categoryName
) {
    public static ProductCategoryResponseDto from(Category category) {
        return new ProductCategoryResponseDto(
                category.getId(),
                category.getName()
        );
    }
}
