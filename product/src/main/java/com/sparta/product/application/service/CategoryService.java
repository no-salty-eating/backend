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
    public Response<Void> createCategory(String categoryName, String role) {
        checkIsMaster(role);
        checkIsExistCategory(categoryName);

        categoryRepository.save(Category.createFrom(categoryName));
        return Response.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .build();
    }

    @Transactional(readOnly = true)
    public Response<CategoryResponseDto> getCategory(Long categoryId, String role) {
        return Response.<CategoryResponseDto>builder()
                .data(role.equals(UserRoleEnum.MASTER.toString()) ?
                        CategoryResponseDto.forMasterFrom(categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new))
                        : CategoryResponseDto.forUserOrSellerFrom(categoryRepository.findByIdAndIsDeletedFalseAndIsPublicTrue(categoryId).orElseThrow(NotFoundCategoryException::new)))
                .build();
    }

    @Transactional
    public Response<Void> updateCategory(Long categoryId, String role, CategoryUpdateRequestDto categoryUpdateRequestDto) {
        checkIsMaster(role);
        if (categoryUpdateRequestDto.categoryName() != null) {
            checkIsExistCategory(categoryUpdateRequestDto);
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new);
        category.updateFrom(categoryUpdateRequestDto.categoryName(), categoryUpdateRequestDto.isPublic());

        return Response.<Void>builder().build();
    }

    @Transactional
    public Response<Void> softDeleteCategory(Long categoryId, String role) {
        checkIsMaster(role);

        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new);
        category.updateIsDeleted(true);

        return Response.<Void>builder().build();
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
