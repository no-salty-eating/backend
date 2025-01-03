package com.sparta.product.domain.core;

import com.sparta.product.application.dtos.category.CategoryRequestDto;
import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@Table(name = "TB_CATEGORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<ProductCategory> productCategoryList = new ArrayList<>();

    public void addProductCategoryList(ProductCategory productCategory) {
        productCategoryList.add(productCategory);
        productCategory.updateCategory(this);
    }

    public static Category createFrom(CategoryRequestDto categoryRequestDto) {
        return Category.builder()
                .name(categoryRequestDto.name())
                .build();
    }
}
