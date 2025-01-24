package com.sparta.product.domain.repository;

import com.sparta.product.domain.core.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
    @Query("SELECT pc FROM ProductCategory pc " +
            "JOIN FETCH pc.product p " +
            "JOIN FETCH pc.category c " +
            "WHERE p.isDeleted = false AND p.isPublic = true " +
            "AND c.isDeleted = false AND c.isPublic = true " +
            "AND pc.product.id = :productId")
    List<ProductCategory> findByProductIdWithConditions(@Param("productId") Long productId);

    @Query("SELECT pc FROM ProductCategory pc " +
            "JOIN FETCH pc.product p " +
            "JOIN FETCH pc.category c " +
            "WHERE pc.product.id = :productId")
    List<ProductCategory> findByProductIdWithoutConditions(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM ProductCategory pc WHERE pc.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
