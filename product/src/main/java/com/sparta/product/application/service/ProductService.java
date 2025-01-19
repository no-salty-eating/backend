package com.sparta.product.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.product.application.dtos.product.ProductRequestDto;
import com.sparta.product.application.dtos.product.ProductResponseDto;
import com.sparta.product.application.dtos.product.ProductUpdateRequestDto;
import com.sparta.product.application.exception.category.NotFoundCategoryException;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.application.exception.product.NotEnoughProductStockException;
import com.sparta.product.application.exception.product.NotFoundProductException;
import com.sparta.product.application.exception.product.NotFoundProductInRedisException;
import com.sparta.product.application.exception.productCategory.NotFoundProductCategoryException;
import com.sparta.product.application.scheduler.redis.RedisKeys;
import com.sparta.product.application.scheduler.redis.RedisManager;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Category;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.ProductCategory;
import com.sparta.product.domain.repository.CategoryRepository;
import com.sparta.product.domain.repository.ProductCategoryRepository;
import com.sparta.product.domain.repository.ProductRepository;
import com.sparta.product.infrastructure.dtos.ProductInternalResponseDto;
import com.sparta.product.infrastructure.kafka.event.OrderSuccessMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final RedisManager redisManager;

    private static final int DEFAULT_REDIS_EXPIRE_SECONDS = 60 * 60 * 2;

    @Transactional
    public void createProduct(ProductRequestDto productRequestDto, String role) {
        checkIsSellerOrMaster(role);

        Product product = Product.createFrom(productRequestDto.productName(), productRequestDto.price(), productRequestDto.stock(), productRequestDto.isPublic());

        productRepository.save(product);

        List<Long> categories = productRequestDto.productCategoryList();
        saveProductCategory(categories, product);

        String cacheKey = RedisKeys.PRODUCT + product.getId();
        redisManager.createHashProduct(product);
        redisManager.setExpireTime(cacheKey, DEFAULT_REDIS_EXPIRE_SECONDS);
    }

    @Transactional(readOnly = true)
    public ProductResponseDto getProduct(Long productId, String role) {
        try {
            String cacheKey = RedisKeys.PRODUCT_DETAIL + productId;
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

            if (!role.equals(UserRoleEnum.MASTER.toString())) {
                saveProductIntoRedis(productId, productCategories, cacheKey, responseDto);
            }

            return responseDto;
        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
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

        // db에 있는 정보 가져오는건가? 그럼 update된 데이터랑 다르지 않나..?
        List<ProductCategory> updatedProductCategories = productCategoryRepository.findByProductIdWithConditions(productId);

        // 캐시 정보 업데이트
        String cacheKey = RedisKeys.PRODUCT_DETAIL + productId;

        ProductResponseDto responseDto = ProductResponseDto.forUserOrSellerOf(updatedProductCategories);
        try {
            String jsonData = objectMapper.writeValueAsString(responseDto);
            if (isEmptyProductInRedis(productId)) {
                throw new NotFoundProductInRedisException();
            }
            redisTemplate.opsForValue().set(cacheKey, jsonData);
            redisManager.setExpireTime(cacheKey, DEFAULT_REDIS_EXPIRE_SECONDS);

            redisManager.updateProductHash(updatedProductCategories.get(0).getProduct());
            redisManager.setExpireTime(RedisKeys.PRODUCT + productId, DEFAULT_REDIS_EXPIRE_SECONDS);

        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }

    }

    private void saveProductIntoRedis(Long productId, List<ProductCategory> updatedProductCategories, String cacheKey, ProductResponseDto responseDto) throws JsonProcessingException {
        String jsonData = objectMapper.writeValueAsString(responseDto);
        redisTemplate.opsForValue().set(cacheKey, jsonData);
        redisManager.setExpireTime(cacheKey, DEFAULT_REDIS_EXPIRE_SECONDS);

        if (isEmptyProductInRedis(productId)) {
            redisManager.createHashProduct(updatedProductCategories.get(0).getProduct());
        }
        redisManager.setExpireTime(RedisKeys.PRODUCT + productId, DEFAULT_REDIS_EXPIRE_SECONDS);
    }

    public boolean isEmptyProductInRedis(Long productId) {
        String cacheKey = RedisKeys.PRODUCT + productId;
        Map<Object, Object> productInfo = redisTemplate.opsForHash().entries(cacheKey);

        return productInfo.isEmpty();
    }

    @Transactional
    public void softDeleteProduct(Long productId, String role) {
        checkIsSellerOrMaster(role);

        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        product.updateIsDeleted(true);
    }

    public void stockManagementInRedis(String serializedMessage) {
        try {
            /**
             * hash에 저장된 timesale, product 재고 감소
             * product detail의 재고 감소
             */
            OrderSuccessMessage decreaseMessage = objectMapper.readValue(serializedMessage, OrderSuccessMessage.class);
            Long productId = decreaseMessage.productId();
            Integer stock = decreaseMessage.quantity();

            String timeSaleKey = RedisKeys.TIMESALE + productId;
            Map<Object, Object> timeSaleInfo = redisTemplate.opsForHash().entries(timeSaleKey);

            String cacheKey = RedisKeys.PRODUCT + productId;
            String detailCacheKey = RedisKeys.PRODUCT_DETAIL + productId;
            String getValue = redisTemplate.opsForValue().get(detailCacheKey);
            ProductResponseDto productResponseDto = objectMapper.readValue(getValue, ProductResponseDto.class);

            if (!timeSaleInfo.isEmpty()) {
                timeSaleService.decreaseTimeSaleProductInRedis(productId, stock);
                decreaseStockProductInRedis(productResponseDto, stock, cacheKey, detailCacheKey);
                redisManager.increaseHashStock(productId, stock, cacheKey);
                increaseStockProductInRedis(productResponseDto, stock, cacheKey);
            } else {
                if (productResponseDto != null) {
                    decreaseStockProductInRedis(productResponseDto, stock, cacheKey, detailCacheKey);
                    increaseStockProductInRedis(productResponseDto, stock, cacheKey);
                }
            }
        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void stockManagementInDb(String serializedMessage) {
        try {
            OrderSuccessMessage decreaseMessage = objectMapper.readValue(serializedMessage, OrderSuccessMessage.class);
            Long productId = decreaseMessage.productId();
            Integer quantity = decreaseMessage.quantity();

            Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);


            if (timeSaleService.isEmptyTimeSaleInRedis(productId)) {
                if (product.getStock() <= 0 || product.getStock() < quantity) {
                    throw new NotEnoughProductStockException();
                }
                product.decreaseStock(quantity);
                product.increaseStock(quantity);
            } else {
                product.decreaseStock(quantity);
                timeSaleService.decreaseTimeSaleStockInDB(productId, quantity);
                product.increaseStock(quantity);
                timeSaleService.increaseTimeSaleStockInDb(productId, quantity);
            }
        } catch (JsonProcessingException e) {
            // TODO : 예외 만들기
            throw new RuntimeException(e);
        }
    }

    public void decreaseStockProductInRedis(ProductResponseDto productResponseDto, Integer stock, String cacheKey, String detailCacheKey) {
        try {
            int currentStock = productResponseDto.stock() != null ? productResponseDto.stock() : 0;

            if (currentStock >= stock) {
                // product detail
                int newStock = currentStock - stock;
                ProductResponseDto updatedProduct = ProductResponseDto.fromDto(productResponseDto, newStock);

                String jsonProduct = objectMapper.writeValueAsString(updatedProduct);
                redisTemplate.opsForValue().set(detailCacheKey, jsonProduct);

                // hash product

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
        String cacheKey = RedisKeys.PRODUCT + productId;

        String jsonData = objectMapper.writeValueAsString(updatedDto);
        redisTemplate.opsForValue().set(cacheKey, jsonData);
    }

    public void deleteCache(Long productId) {
        String cacheKey = RedisKeys.PRODUCT + productId;

        redisTemplate.delete(cacheKey);
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
        if (!(role.equals(UserRoleEnum.MASTER.getAuthority()) || role.equals(UserRoleEnum.SELLER.getAuthority()))) {
            log.info("forbidden role in checkIsSellerOrMaster: {}", role);
            throw new ForbiddenRoleException();
        }
    }

    private void checkIsMaster(String role) {
        if (!role.equals(UserRoleEnum.MASTER.getAuthority())) {
            log.info("forbidden role in checkIsMaster: {}", role);
            throw new ForbiddenRoleException();
        }
    }
}
