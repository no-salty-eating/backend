package com.sparta.product.domain.core;

import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "TB_CATEGORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    public static Category createFrom(String categoryName) {
        return new Category(categoryName);
    }

    private Category(String categoryName) {
        this.name = categoryName;
    }

    public void updateFrom(String categoryName, Boolean isPublic) {
        if (categoryName != null) {
            name = categoryName;
        }

        if (isPublic != null) {
            super.updateIsPublic(isPublic);
        }
    }
}
