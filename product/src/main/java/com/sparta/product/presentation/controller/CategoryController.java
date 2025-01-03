package com.sparta.product.presentation.controller;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.category.CategoryRequestDto;
import com.sparta.product.application.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    public Response<Void> createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto,
                                         @RequestHeader(name = "X-UserId", required = false) String userId,
                                         @RequestHeader(name = "X-Role") String role) {
        return categoryService.createCategory(categoryRequestDto, role);
    }
}
