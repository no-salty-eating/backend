package com.sparta.product.domain.repository;

import com.sparta.product.domain.core.Product;
import com.sparta.product.infrastructure.repository.CustomProductRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {
    Optional<Product> findByIdAndIsDeletedFalse(Long id);

    Optional<Product> findByIdAndIsDeletedFalseAndIsPublicTrue(Long id);

   List<Product> findAllByIdInAndIsDeletedFalse(List<Long> productIdList);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId AND p.isDeleted = false")
    Optional<Product> findByIdAndIsDeletedFalseWithLock(Long productId);
}
