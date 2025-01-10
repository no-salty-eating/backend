package com.sparta.product.presentation.controller;

import com.sparta.product.application.dtos.product.ProductRequestDto;
import com.sparta.product.application.dtos.product.ProductResponseDto;
import com.sparta.product.application.dtos.product.ProductUpdateRequestDto;
import com.sparta.product.application.service.ProductService;
import com.sparta.product.infrastructure.dtos.ProductInternalResponseDto;
import com.sparta.product.infrastructure.kafka.event.StockDecreaseMessage;
import com.sparta.product.presentation.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        productService.createProduct(productRequestDto, role);
        return Response.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .build();
    }

    @GetMapping("/{productId}")
    public Response<ProductResponseDto> getProduct(@PathVariable Long productId,
                                                   @RequestHeader(name = "X-UserId", required = false) String userId,
                                                   @RequestHeader(name = "X-Role") String role) {
        return Response.<ProductResponseDto>builder()
                .data(productService.getProduct(productId, role))
                .build();
    }

    @PatchMapping("/{productId}")
    public Response<ProductResponseDto> updateProduct(@PathVariable Long productId,
                                                      @RequestBody @Valid ProductUpdateRequestDto productUpdateRequestDto,
                                                      @RequestHeader(name = "X-UserId", required = false) String userId,
                                                      @RequestHeader(name = "X-Role") String role) {
        return Response.<ProductResponseDto>builder()
                .data(productService.updateProduct(productId, role, productUpdateRequestDto))
                .build();
    }

    @DeleteMapping("/{productId}")
    public Response<Void> softDeleteProduct(@PathVariable Long productId,
                                            @RequestHeader(name = "X-UserId", required = false) String userId,
                                            @RequestHeader(name = "X-Role") String role) {
        productService.softDeleteProduct(productId, role);
        return Response.<Void>builder().build();
    }

    @PostMapping("/product-stock-decrease-db")
    public Response<Void> decreaseStockInDb(@RequestBody StockDecreaseMessage stockDecreaseMessage) {
        productService.decreaseStockInDb(stockDecreaseMessage.productId(), stockDecreaseMessage.stock());
        return Response.<Void>builder().build();
    }

    @GetMapping("/internal/{productId}")
    public ProductInternalResponseDto internalGetProduct(@PathVariable Long productId) {
        return productService.internalGetProduct(productId);
    }
}
