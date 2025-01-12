package com.sparta.product.infrastructure.dtos;

import com.sparta.product.domain.core.Product;

public record ProductInternalResponseDto(
        Long productId,
        String name,
        Integer price,
        Integer stock
) {
    public static ProductInternalResponseDto createFrom(Product product) {
        return new ProductInternalResponseDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock()
        );
    }
}
