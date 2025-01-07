package com.sparta.product.application.dtos.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.application.dtos.productCategory.ProductCategoryResponseDto;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.core.Product;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductResponseDto(
        @JsonProperty("product_id") Long productId,
        @JsonProperty("product_category_list") List<ProductCategoryResponseDto> productCategoryList,
        @JsonProperty("product_name") String productName,
        Integer price,
        Integer stock,
        @JsonProperty("is_deleted") Boolean isDeleted,
        @JsonProperty("is_public") Boolean isPublic,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
        ) {

    public static ProductResponseDto forUserOrSellerOf(Product product, List<Category> categories) {
        return new ProductResponseDto(
                product.getId(),
                categories.stream()
                        .map(ProductCategoryResponseDto::from)
                        .toList(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }

    public static ProductResponseDto forMasterOf(Product product, List<Category> categories) {
        return new ProductResponseDto(
                product.getId(),
                categories.stream()
                        .map(ProductCategoryResponseDto::from)
                        .toList(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.isDeleted(),
                product.isPublic(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private ProductResponseDto(Long productId, List<ProductCategoryResponseDto> categories, String productName, Integer price, Integer stock) {
        this(productId, categories, productName, price, stock, null, null, null, null);
    }
}
