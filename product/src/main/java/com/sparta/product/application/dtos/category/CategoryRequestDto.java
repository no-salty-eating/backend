package com.sparta.product.application.dtos.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryRequestDto(@JsonProperty("category_name") @NotBlank(message = "카테고리 이름 입력은 필수입니다.") String categoryName) {
}