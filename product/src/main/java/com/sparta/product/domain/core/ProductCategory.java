package com.sparta.product.domain.core;

import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "TB_PRODUCT_CATEGORY")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public void updateProduct(Product product) {
        this.product = product;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public static ProductCategory createOf(Product product, Category category) {
        return new ProductCategory(product, category);
    }

    private ProductCategory(Product product, Category category) {
        this.product = product;
        this.category = category;
    }
}
