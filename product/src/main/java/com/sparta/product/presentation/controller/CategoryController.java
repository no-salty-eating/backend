package com.sparta.product.presentation.controller;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.category.CategoryResponseDto;
import com.sparta.product.application.dtos.category.CategoryUpdateRequestDto;
import com.sparta.product.application.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public Response<Void> createCategory(@RequestBody String categoryName,
                                         @RequestHeader(name = "X-UserId", required = false) String userId,
                                         @RequestHeader(name = "X-Role") String role) {
        categoryService.createCategory(categoryName, role);
        return Response.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .build();
    }

    @GetMapping("/{categoryId}")
    public Response<CategoryResponseDto> getCategory(@PathVariable Long categoryId,
                                                     @RequestHeader(name = "X-UserId", required = false) String userId,
                                                     @RequestHeader(name = "X-Role") String role) {
        return Response.<CategoryResponseDto>builder()
                .data(categoryService.getCategory(categoryId, role))
                .build();
    }

    @PatchMapping("/{categoryId}")
    public Response<Void> updateCategory(@PathVariable Long categoryId,
                                         @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto,
                                         @RequestHeader(name = "X-UserId", required = false) String userId,
                                         @RequestHeader(name = "X-Role") String role) {
        categoryService.updateCategory(categoryId, role, categoryUpdateRequestDto);
        return Response.<Void>builder().build();
    }

    @DeleteMapping("/{categoryId}")
    public Response<Void> softDeleteCategory(@PathVariable Long categoryId,
                                             @RequestHeader(name = "X-UserId", required = false) String userId,
                                             @RequestHeader(name = "X-Role") String role) {
        categoryService.softDeleteCategory(categoryId, role);
        return Response.<Void>builder().build();
    }
}
