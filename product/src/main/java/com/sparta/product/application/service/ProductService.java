package com.sparta.product.application.service;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.product.ProductRequestDto;
import com.sparta.product.application.exception.category.NotFoundCategoryException;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.ProductCategory;
import com.sparta.product.domain.repository.CategoryRepository;
import com.sparta.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Response<Void> createProduct(ProductRequestDto productRequestDto, String role) {
        checkIsSellerOrMaster(role);

        Product product = Product.createFrom(productRequestDto);

        List<Long> categoryIds = productRequestDto.productCategoryList();

        // 쿼리 목록 리스트로 한번에 가져오기
        Map<Long, Category> categoryMap = categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(Category::getId,
                        category -> category));

        for (Long categoryId : categoryIds) {
            Category findCategory = categoryMap.get(categoryId);
            if (findCategory == null) {
                throw new NotFoundCategoryException();
            }
            ProductCategory productCategory = ProductCategory.createOf(product, findCategory);
            product.addProductCategoryList(productCategory);
        }

        productRepository.save(product);
        return new Response<>(HttpStatus.CREATED.value(), "상품 등록 완료", null);
    }

    private void checkIsSellerOrMaster(String role) {
        if (!(role.equals(UserRoleEnum.MASTER.toString()) || role.equals(UserRoleEnum.SELLER.toString()))) {
            log.info("forbidden role : {}", role);
            throw new ForbiddenRoleException();
        }
    }
}
