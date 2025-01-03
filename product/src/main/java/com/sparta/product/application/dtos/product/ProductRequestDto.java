package com.sparta.product.application.dtos.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.application.dtos.productCategory.ProductCategoryRequestDto;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductRequestDto(@JsonProperty("product_category_list") List<ProductCategoryRequestDto> productCategoryRequestDto,
                                @JsonProperty("user_id") Long userId,
                                @JsonProperty("product_name") String productName,
                                Integer price,
                                Integer stock) {
}
