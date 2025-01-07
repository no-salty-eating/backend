package com.sparta.product.domain.repository;

import com.sparta.product.domain.core.Category;
import com.sparta.product.infrastructure.repository.CustomCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {
    List<Category> findAllByIdInAndIsDeletedFalse(List<Long> categoryIds);

    Optional<Category> findByIdAndIsDeletedFalseAndIsPublicTrue(Long id);

    Boolean existsByName(String name);
}
