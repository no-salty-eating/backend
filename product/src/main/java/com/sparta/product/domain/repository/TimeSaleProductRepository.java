package com.sparta.product.domain.repository;

import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.infrastructure.repository.CustomTimeSaleProductRepository;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface TimeSaleProductRepository extends JpaRepository<TimeSaleProduct, Long>, CustomTimeSaleProductRepository {
    Optional<TimeSaleProduct> findByIdAndIsDeletedFalseAndIsPublicTrue(Long productId);

    boolean existsByProductIdAndTimeSaleEndTimeAfter(Long id, LocalDateTime now);

    @Query("SELECT ts FROM TimeSaleProduct ts JOIN FETCH ts.product WHERE ts.product.id = :productId AND ts.isDeleted = false AND ts.isPublic = true AND ts.product.isDeleted = false")
    Optional<TimeSaleProduct> findByProductIdAndIsDeletedFalseAndIsPublicTrue(@Param("productId") Long productId);
}
