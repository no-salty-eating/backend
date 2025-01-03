package com.sparta.product.presentation.controller;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.product.ProductRequestDto;
import com.sparta.product.application.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Response<Void> createProduct(@RequestBody @Valid ProductRequestDto productRequestDto,
                                        @RequestHeader(name = "X-UserId", required = false) String userId,
                                        @RequestHeader(name = "X-Role") String role) {
        return productService.createProduct(productRequestDto, role);
    }
}
