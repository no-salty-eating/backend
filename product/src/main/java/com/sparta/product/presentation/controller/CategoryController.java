package com.sparta.product.presentation.controller;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.category.CategoryRequestDto;
import com.sparta.product.application.dtos.category.CategoryResponseDto;
import com.sparta.product.application.dtos.category.CategoryUpdateRequestDto;
import com.sparta.product.application.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Response<Void> createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto,
                                         @RequestHeader(name = "X-UserId", required = false) String userId,
                                         @RequestHeader(name = "X-Role") String role) {
        return categoryService.createCategory(categoryRequestDto, role);
    }

    @GetMapping("/{categoryId}")
    public Response<CategoryResponseDto> getCategory(@PathVariable Long categoryId,
                                                     @RequestHeader(name = "X-UserId", required = false) String userId,
                                                     @RequestHeader(name = "X-Role") String role) {
        return categoryService.getCategory(categoryId, role);
    }

    @PatchMapping("/{categoryId}")
    public Response<Void> updateCategory(@PathVariable Long categoryId,
                                         @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto,
                                         @RequestHeader(name = "X-UserId", required = false) String userId,
                                         @RequestHeader(name = "X-Role") String role) {
        return categoryService.updateCategory(categoryId, role, categoryUpdateRequestDto);
    }

    @DeleteMapping("/{categoryId}")
    public Response<Void> softDeleteCategory(@PathVariable Long categoryId,
                                             @RequestHeader(name = "X-UserId", required = false) String userId,
                                             @RequestHeader(name = "X-Role") String role) {
        return categoryService.softDeleteCategory(categoryId, role);
    }
}
