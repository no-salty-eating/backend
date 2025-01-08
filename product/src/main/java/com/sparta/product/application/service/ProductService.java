package com.sparta.product.application.service;

import com.sparta.product.application.dtos.product.ProductRequestDto;
import com.sparta.product.application.dtos.product.ProductResponseDto;
import com.sparta.product.application.dtos.product.ProductUpdateRequestDto;
import com.sparta.product.application.exception.category.NotFoundCategoryException;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.application.exception.product.NotFoundProductException;
import com.sparta.product.application.exception.productCategory.NotFoundProductCategoryException;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.ProductCategory;
import com.sparta.product.domain.repository.CategoryRepository;
import com.sparta.product.domain.repository.ProductCategoryRepository;
import com.sparta.product.domain.repository.ProductRepository;
import com.sparta.product.presentation.Response;
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
    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public void createProduct(ProductRequestDto productRequestDto, String role) {
        checkIsSellerOrMaster(role);

        Product product = Product.createFrom(productRequestDto.productName(), productRequestDto.price(), productRequestDto.stock(), productRequestDto.isPublic());

        productRepository.save(product);

        List<Long> categories = productRequestDto.productCategoryList();
        saveProductCategory(categories, product);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId, String role) {
        List<ProductCategory> productCategories = role.equals(UserRoleEnum.MASTER.toString())
                ? productCategoryRepository.findByProductIdWithoutConditions(productId)
                : productCategoryRepository.findByProductIdWithConditions(productId);

        if (productCategories.isEmpty()) {
            throw new NotFoundProductCategoryException();
        }

        return role.equals(UserRoleEnum.MASTER.toString()) ?
                ProductResponseDto.forMasterOf(productCategories)
                : ProductResponseDto.forUserOrSellerOf(productCategories);
    }

    @Transactional
    public void updateProduct(Long productId, String role, ProductUpdateRequestDto productUpdateRequestDto) {
        checkIsSellerOrMaster(role);

        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        List<Long> categories = productUpdateRequestDto.productCategoryList();
        if (categories != null && !categories.isEmpty()) {
            productCategoryRepository.deleteByProductId(productId);
            saveProductCategory(categories, product);
        }
        product.updateFrom(productUpdateRequestDto.productName(), productUpdateRequestDto.price(), productUpdateRequestDto.stock(), productUpdateRequestDto.isPublic());
    }

    @Transactional
    public void softDeleteProduct(Long productId, String role) {
        checkIsSellerOrMaster(role);

        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        product.updateIsDeleted(true);
    }

    private void saveProductCategory(List<Long> categories, Product product) {
        Map<Long, Category> categoryMap = getCategoryMap(categories);
        categories.forEach(categoryId -> {
            Category category = categoryMap.get(categoryId);
            if (category == null) {
                throw new NotFoundCategoryException();
            }
            ProductCategory productCategory = ProductCategory.createOf(product, category);
            productCategoryRepository.save(productCategory);
        });
    }

    private Map<Long, Category> getCategoryMap(List<Long> categories) {
        return categoryRepository.findAllByIdInAndIsDeletedFalse(categories)
                .stream()
                .collect(Collectors.toMap(Category::getId,
                        category -> category));
    }

    private void checkIsSellerOrMaster(String role) {
        if (!(role.equals(UserRoleEnum.MASTER.toString()) || role.equals(UserRoleEnum.SELLER.toString()))) {
            log.info("forbidden role in checkIsSellerOrMaster: {}", role);
            throw new ForbiddenRoleException();
        }
    }

    private void checkIsMaster(String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString())) {
            log.info("forbidden role in checkIsMaster: {}", role);
            throw new ForbiddenRoleException();
        }
    }
}
