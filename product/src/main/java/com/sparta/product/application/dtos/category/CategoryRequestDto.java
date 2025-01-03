package com.sparta.product.application.dtos.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryRequestDto(@JsonProperty("category_name") String name) {
}