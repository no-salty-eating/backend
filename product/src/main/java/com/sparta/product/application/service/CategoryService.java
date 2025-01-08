package com.sparta.product.application.service;

import com.sparta.product.application.dtos.category.CategoryResponseDto;
import com.sparta.product.application.dtos.category.CategoryUpdateRequestDto;
import com.sparta.product.application.exception.category.AlreadyExistCategoryException;
import com.sparta.product.application.exception.category.NotFoundCategoryException;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.repository.CategoryRepository;
import com.sparta.product.presentation.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(String categoryName, String role) {
        checkIsMaster(role);
        checkIsExistCategory(categoryName);

        categoryRepository.save(Category.createFrom(categoryName));
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategory(Long categoryId, String role) {
        Category category = role.equals(UserRoleEnum.MASTER.toString()) ?
                categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new)
                : categoryRepository.findByIdAndIsDeletedFalseAndIsPublicTrue(categoryId).orElseThrow(NotFoundCategoryException::new);

        return role.equals(UserRoleEnum.MASTER.toString()) ?
                        CategoryResponseDto.forMasterFrom(category)
                        : CategoryResponseDto.forUserOrSellerFrom(category);
    }

    @Transactional
    public void updateCategory(Long categoryId, String role, CategoryUpdateRequestDto categoryUpdateRequestDto) {
        checkIsMaster(role);
        if (categoryUpdateRequestDto.categoryName() != null) {
            checkIsExistCategory(categoryUpdateRequestDto);
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new);
        category.updateFrom(categoryUpdateRequestDto.categoryName(), categoryUpdateRequestDto.isPublic());
    }

    @Transactional
    public void softDeleteCategory(Long categoryId, String role) {
        checkIsMaster(role);

        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new);
        category.updateIsDeleted(true);
    }

    private void checkIsExistCategory(CategoryUpdateRequestDto categoryUpdateRequestDto) {
        if (categoryRepository.existsByName(categoryUpdateRequestDto.categoryName())) {
            throw new AlreadyExistCategoryException();
        }
    }

    private void checkIsExistCategory(String categoryName) {
        if (categoryRepository.existsByName(categoryName)) {
            log.info("already exist category : {}", categoryName);
            throw new AlreadyExistCategoryException();
        }
    }

    private void checkIsMaster(String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString())) {
            log.info("forbidden role : {}", role);
            throw new ForbiddenRoleException();
        }
    }
}
