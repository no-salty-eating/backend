package com.sparta.product.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.product.application.dtos.product.ProductRequestDto;
import com.sparta.product.application.dtos.product.ProductResponseDto;
import com.sparta.product.application.dtos.product.ProductUpdateRequestDto;
import com.sparta.product.application.exception.category.NotFoundCategoryException;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.application.exception.product.NotFoundProductException;
import com.sparta.product.application.exception.productCategory.NotFoundProductCategoryException;
import com.sparta.product.application.scheduler.redis.RedisKeys;
import com.sparta.product.application.scheduler.redis.TimeSaleRedisManager;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.ProductCategory;
import com.sparta.product.domain.repository.CategoryRepository;
import com.sparta.product.domain.repository.ProductCategoryRepository;
import com.sparta.product.domain.repository.ProductRepository;
import com.sparta.product.infrastructure.dtos.ProductInternalResponseDto;
import com.sparta.product.infrastructure.kafka.event.StockDecreaseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
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
    private final RedisTemplate<String, String> redisTemplate;
    private final TimeSaleService timeSaleService;
    private final ObjectMapper objectMapper;
    private final TimeSaleRedisManager timeSaleRedisManager;
    private static final String PRODUCT = "product:";

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
        try {
            String cacheKey = PRODUCT + productId;

            String cachedData = redisTemplate.opsForValue().get(cacheKey);

            if (cachedData != null) {
                return objectMapper.readValue(cachedData, ProductResponseDto.class);
            }

            List<ProductCategory> productCategories = role.equals(UserRoleEnum.MASTER.toString())
                    ? productCategoryRepository.findByProductIdWithoutConditions(productId)
                    : productCategoryRepository.findByProductIdWithConditions(productId);

            if (productCategories.isEmpty()) {
                throw new NotFoundProductCategoryException();
            }

            ProductResponseDto responseDto = role.equals(UserRoleEnum.MASTER.toString())
                    ? ProductResponseDto.forMasterOf(productCategories)
                    : ProductResponseDto.forUserOrSellerOf(productCategories);

            String jsonData = objectMapper.writeValueAsString(responseDto);
            redisTemplate.opsForValue().set(cacheKey, jsonData);

            return responseDto;
        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
    }

    // TODO : 업데이트 시에도 수정된 값 캐싱으로 넣어주기.
    @Transactional
    public ProductResponseDto updateProduct(Long productId, String role, ProductUpdateRequestDto productUpdateRequestDto) {
        checkIsSellerOrMaster(role);

        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        List<Long> categories = productUpdateRequestDto.productCategoryList();
        if (categories != null && !categories.isEmpty()) {
            productCategoryRepository.deleteByProductId(productId);
            saveProductCategory(categories, product);
        }
        product.updateFrom(productUpdateRequestDto.productName(), productUpdateRequestDto.price(), productUpdateRequestDto.stock(), productUpdateRequestDto.isPublic());

        List<ProductCategory> updatedProductCategories = productCategoryRepository.findByProductIdWithoutConditions(productId);

        return ProductResponseDto.forUserOrSellerOf(updatedProductCategories);
    }

    @CacheEvict(cacheNames = RedisKeys.PRODUCT, key = "#productId")
    @Transactional
    public void softDeleteProduct(Long productId, String role) {
        checkIsSellerOrMaster(role);

        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        product.updateIsDeleted(true);
    }

    public void stockManagementInRedis(String serializedMessage) {
        try {
            /**
             * 1. productId로 timesale:on:{productId}에 해당하는 값이 있는지 확인하기
             * 2. 있다면 감소/증가 확인 후 적용. 없다면 product:{productId}에서 증가, 감소 적용..
             */
            StockDecreaseMessage decreaseMessage = objectMapper.readValue(serializedMessage, StockDecreaseMessage.class);
            Long productId = decreaseMessage.productId();
            Integer stock = decreaseMessage.stock();
            Boolean isDecrease = decreaseMessage.isDecrease();

            String timeSaleKey = RedisKeys.TIMESALE_ON + productId;
            Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(timeSaleKey);

            if (!productInfo.isEmpty()) {
                if (isDecrease) {
                    timeSaleService.decreaseTimeSaleProductInRedis(productId, stock);
                } else {
                    timeSaleRedisManager.increaseInventory(productId, stock);
                }
            } else {
                String cacheKey = PRODUCT + productId;
                String getValue = redisTemplate.opsForValue().get(cacheKey);
                ProductResponseDto productResponseDto = objectMapper.readValue(getValue, ProductResponseDto.class);
                if (productResponseDto != null) {
                    if (isDecrease) {
                        decreaseStockProductInRedis(productResponseDto, stock, cacheKey);
                    } else {
                        increaseStockProductInRedis(productResponseDto, stock, cacheKey);
                    }
                }
            }
        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
    }

    public void stockManagementInDb(String serializedMessage) {
        try {
            StockDecreaseMessage decreaseMessage = objectMapper.readValue(serializedMessage, StockDecreaseMessage.class);
            Long productId = decreaseMessage.productId();
            Integer stock = decreaseMessage.stock();
            Boolean isDecrease = decreaseMessage.isDecrease();

            String timeSaleKey = RedisKeys.TIMESALE_ON + productId;
            Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(timeSaleKey);


        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
    }

    public void decreaseStockProductInRedis(ProductResponseDto productResponseDto, Integer stock, String cacheKey) {
        try {
            int currentStock = productResponseDto.stock() != null ? productResponseDto.stock() : 0;

            if (currentStock >= stock) {
                int newStock = currentStock - stock;

                ProductResponseDto updatedProduct = ProductResponseDto.fromDto(productResponseDto, newStock);

                String jsonProduct = objectMapper.writeValueAsString(updatedProduct);
                redisTemplate.opsForValue().set(cacheKey, jsonProduct);

            }
        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
    }

    public void increaseStockProductInRedis(ProductResponseDto productResponseDto, Integer stock, String cacheKey) {
        try {
            if (productResponseDto != null) {
                int currentStock = productResponseDto.stock() != null ? productResponseDto.stock() : 0;
                int newStock = currentStock + stock;

                // 새로운 ProductResponseDto 객체 생성
                ProductResponseDto updatedProduct = ProductResponseDto.fromDto(productResponseDto, newStock);

                String jsonProduct = objectMapper.writeValueAsString(updatedProduct);
                redisTemplate.opsForValue().set(cacheKey, jsonProduct);
            }
        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
    }

    public void updateCache(Long productId, ProductResponseDto updatedDto) throws JsonProcessingException {
        String cacheKey = PRODUCT + productId;

        String jsonData = objectMapper.writeValueAsString(updatedDto);
        redisTemplate.opsForValue().set(cacheKey, jsonData);
    }

    public void deleteCache(Long productId) {
        String cacheKey = PRODUCT + productId;

        redisTemplate.delete(cacheKey);
    }
    // TODO : 예외 발생 시 redis와 동기화 처리가 필요함

    @Async
    @Transactional
    public void decreaseStockInDb(Long productId, Integer stock) {
        Product product = productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(NotFoundProductException::new);
        product.decreaseStock(stock);
        productRepository.save(product);
    }

    public ProductInternalResponseDto internalGetProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(NotFoundProductException::new);

        return ProductInternalResponseDto.createFrom(product);
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
