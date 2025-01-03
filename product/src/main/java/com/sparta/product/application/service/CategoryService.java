package com.sparta.product.application.service;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.category.CategoryRequestDto;
import com.sparta.product.application.exception.category.AlreadyExistCategoryException;
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

    private void checkIsExistCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRepository.findByName(categoryRequestDto.categoryName()).isPresent()) {
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
