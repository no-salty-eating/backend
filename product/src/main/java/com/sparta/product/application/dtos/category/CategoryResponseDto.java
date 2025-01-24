package com.sparta.product.application.dtos.category;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.product.domain.core.Category;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryResponseDto(
        @JsonProperty("category_id") Long categoryId,
        @JsonProperty("category_name") String categoryName,
        @JsonProperty("is_deleted") Boolean isDeleted,
        @JsonProperty("is_public") Boolean isPublic,
        @JsonProperty("created_at") LocalDateTime createdAt,
        @JsonProperty("updated_at") LocalDateTime updatedAt
) {

    // TODO : 추후 dto 분리 고려 예정
    public static CategoryResponseDto forUserOrSellerFrom(Category category) {
        return CategoryResponseDto.builder()
                .categoryId(category.getId())
                .categoryName(category.getName())
                .build();
    }

    public static CategoryResponseDto forMasterFrom(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getIsDeleted(),
                category.getIsPublic(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

}
