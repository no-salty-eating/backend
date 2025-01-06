package com.sparta.product.application.dtos.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryUpdateRequestDto(
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("is_public") Boolean isPublic
) {
}
