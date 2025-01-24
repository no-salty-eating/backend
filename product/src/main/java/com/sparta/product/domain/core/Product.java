package com.sparta.product.domain.core;

import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@DynamicUpdate
@Table(name = "TB_PRODUCT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer price;
    private Integer stock;

    public static Product createFrom(String productName, Integer price, Integer stock, Boolean isPublic) {
        return new Product(
                productName,
                price,
                stock,
                isPublic
        );
    }

    private Product(String name, Integer price, Integer stock, Boolean isPublic) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        super.updateIsPublic(isPublic);
    }

    public void updateFrom(String productName, Integer price, Integer stock, Boolean isPublic) {
        if (productName != null) {
            name = productName;
        }

        if (price != null) {
            this.price = price;
        }

        if (stock != null) {
            this.stock = stock;
        }

        if (isPublic != null) {
            super.updateIsPublic(isPublic);
        }
    }

    public void decreaseStock(Integer quantity) {
        stock -= quantity;
    }

    public void increaseStock(Integer quantity) {
        stock += quantity;
    }
}
