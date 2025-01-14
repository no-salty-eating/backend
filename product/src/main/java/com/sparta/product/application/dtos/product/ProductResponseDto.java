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
        @JsonProperty("name") String name,
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
                .name(product.getName())
                .price(product.getPrice())
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
                product.getIsDeleted(),
                product.getIsPublic(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public static ProductResponseDto fromDto(ProductResponseDto productResponseDto, Integer newStock) {
        return ProductResponseDto.builder()
                .productId(productResponseDto.productId())
                .productCategoryList(productResponseDto.productCategoryList())
                .name(productResponseDto.name())
                .price(productResponseDto.price())
                .stock(newStock)
                .build();
    }
}
