package com.sparta.product.application.dtos.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductRequestDto(@JsonProperty("product_category_list") @NotNull(message = "카테고리 입력은 필수입니다.") List<Long> productCategoryList,
                                @JsonProperty("product_name") @NotBlank(message = "상품 이름 입력은 필수입니다.") String productName,
                                @NotNull(message = "가격 입력은 필수입니다.") @Positive(message = "가격은 양수값이어야 합니다.") Integer price,
                                @NotNull(message = "수량 입력은 필수입니다.") @Positive(message = "수량은 양수값이어야 합니다.") Integer stock) {
}
