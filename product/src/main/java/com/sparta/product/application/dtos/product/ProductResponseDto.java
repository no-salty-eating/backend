package com.sparta.product.application.dtos.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.application.dtos.productCategory.ProductCategoryResponseDto;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.ProductCategory;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
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

    // TODO : 추후 dto 분리 고려 예정
    public static ProductResponseDto forUserOrSellerOf(List<ProductCategory> productCategories) {
        Product product = productCategories.get(0).getProduct();
        List<Category> categories = productCategories.stream()
                .map(ProductCategory::getCategory)
                .toList();

        return ProductResponseDto.builder()
                .productId(product.getId())
                .productCategoryList(categories.stream()
                        .map(ProductCategoryResponseDto::from)
                        .toList())
                .productName(product.getName())
                .price(product.getPrice())
                .stock(product.getStock())
                .build();
    }

    public static ProductResponseDto forMasterOf(List<ProductCategory> productCategories) {
        Product product = productCategories.get(0).getProduct();
        List<Category> categories = productCategories.stream()
                .map(ProductCategory::getCategory)
                .toList();

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
}
