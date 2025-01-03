package com.sparta.product.domain.core;

import com.sparta.product.application.dtos.product.ProductRequestDto;
import com.sparta.product.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductCategory> productCategoryList = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimeSaleProduct> timeSaleProductList = new ArrayList<>();

    public void addProductCategoryList(ProductCategory productCategory) {
        productCategoryList.add(productCategory);
        productCategory.updateProduct(this);
    }

    public void addTimeSaleProductList(TimeSaleProduct timeSaleProduct) {
        timeSaleProductList.add(timeSaleProduct);
        timeSaleProduct.updateProduct(this);
    }

    public static Product createFrom(ProductRequestDto productRequestDto) {
        return new Product(
                productRequestDto.productName(),
                productRequestDto.price(),
                productRequestDto.stock(),
                new ArrayList<>()
        );
    }

    public Product(String name, Integer price, Integer stock, List<ProductCategory> productCategoryList) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.productCategoryList = productCategoryList;
    }
}
