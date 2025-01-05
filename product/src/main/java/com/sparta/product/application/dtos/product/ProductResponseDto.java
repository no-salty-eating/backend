package com.sparta.product.application.dtos.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.application.dtos.productCategory.ProductCategoryResponseDto;
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

    public static ProductResponseDto forUserOrSellerFrom(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getProductCategoryList().stream()
                        .map(ProductCategoryResponseDto::from)
                        .toList(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                null,
                null,
                null,
                null
        );
    }

    public static ProductResponseDto forMasterFrom(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getProductCategoryList().stream()
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
}
