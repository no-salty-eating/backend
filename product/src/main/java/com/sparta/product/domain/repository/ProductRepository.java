package com.sparta.product.domain.repository;

import com.sparta.product.domain.core.Product;
import com.sparta.product.infrastructure.repository.CustomProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {
    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Optional<Product> findByIdAndIsDeletedFalseAndIsPublicTrue(Long id);
}
