package com.sparta.product.application.dtos.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductUpdateRequestDto(@JsonProperty("product_category_list") List<Long> productCategoryList,
                                      @JsonProperty("product_name") String productName,
                                      @Positive(message = "가격은 양수값이어야 합니다.") Integer price,
                                      @Positive(message = "수량은 양수값이어야 합니다.") Integer stock,
                                      @JsonProperty("is_public") Boolean isPublic) {
}
