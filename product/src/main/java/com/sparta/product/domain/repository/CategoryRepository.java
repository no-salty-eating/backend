package com.sparta.product.domain.repository;

import com.sparta.product.domain.core.Category;
import com.sparta.product.infrastructure.repository.CustomCategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {
    Optional<Category> findByName(String categoryName);

    List<Category> findAllByNameIn(List<String> categories);

//    @Query("select c from Category c where c.id in :categoryIds And c.isDeleted = false")
    List<Category> findAllByIdInAndIsDeletedFalse(@Param("categoryIds") List<Long> categoryIds);

    Optional<Category> findByIdAndIsDeletedFalseAndIsPublicTrue(Long id);

    Boolean existsByName(String name);
}
