package com.sparta.product.application.service;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.category.CategoryRequestDto;
import com.sparta.product.application.dtos.category.CategoryResponseDto;
import com.sparta.product.application.dtos.category.CategoryUpdateRequestDto;
import com.sparta.product.application.exception.category.AlreadyExistCategoryException;
import com.sparta.product.application.exception.category.NotFoundCategoryException;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.repository.CategoryRepository;
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
    public Response<Void> createCategory(CategoryRequestDto categoryRequestDto, String role) {
        checkIsMaster(role);
        checkIsExistCategory(categoryRequestDto);

        categoryRepository.save(Category.createFrom(categoryRequestDto));
        return new Response<>(HttpStatus.CREATED.value(), "카테고리 등록 완료", null);
    }

    @Transactional(readOnly = true)
    public Response<CategoryResponseDto> getCategory(Long categoryId, String role) {
        return new Response<>(HttpStatus.OK.value(),
                "OK",
                role.equals(UserRoleEnum.MASTER.toString()) ?
                        CategoryResponseDto.forMasterFrom(categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new))
                        : CategoryResponseDto.forUserOrSellerFrom(categoryRepository.findByIdAndIsDeletedFalseAndIsPublicTrue(categoryId).orElseThrow(NotFoundCategoryException::new)));
    }

    @Transactional
    public Response<Void> updateCategory(Long categoryId, String role, CategoryUpdateRequestDto categoryUpdateRequestDto) {
        checkIsMaster(role);
        if (categoryUpdateRequestDto.categoryName() != null) {
            checkIsExistCategory(categoryUpdateRequestDto);
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new);
        category.updateFrom(categoryUpdateRequestDto);

        return new Response<>(HttpStatus.OK.value(), "수정 완료.", null);
    }

    @Transactional
    public Response<Void> softDeleteCategory(Long categoryId, String role) {
        checkIsMaster(role);

        Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundCategoryException::new);
        category.updateIsDeleted(true);

        return new Response<>(HttpStatus.OK.value(), "삭제 완료.", null);
    }

    private void checkIsExistCategory(CategoryUpdateRequestDto categoryUpdateRequestDto) {
        if (categoryRepository.existsByName(categoryUpdateRequestDto.categoryName())) {
            throw new AlreadyExistCategoryException();
        }
    }

    private void checkIsExistCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRepository.existsByName(categoryRequestDto.categoryName())) {
            log.info("already exist category : {}", categoryRequestDto.categoryName());
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
