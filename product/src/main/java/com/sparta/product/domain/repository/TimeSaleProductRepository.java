package com.sparta.product.domain.repository;

import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.infrastructure.repository.CustomTimeSaleProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeSaleProductRepository extends JpaRepository<TimeSaleProduct, Long>, CustomTimeSaleProductRepository {
    Optional<TimeSaleProduct> findByIdAndIsDeletedFalseAndIsPublicTrue(Long productId);
}
