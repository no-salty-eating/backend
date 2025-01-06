package com.sparta.product.application.service;

import com.sparta.product.application.dtos.Response;
import com.sparta.product.application.dtos.timesale.TimeSaleProductRequestDto;
import com.sparta.product.application.exception.common.ForbiddenRoleException;
import com.sparta.product.application.exception.product.NotFoundProductException;
import com.sparta.product.application.exception.timesale.TimeSaleQuantityExceedProductStockException;
import com.sparta.product.domain.common.UserRoleEnum;
import com.sparta.product.domain.core.Product;
import com.sparta.product.domain.core.TimeSaleProduct;
import com.sparta.product.domain.repository.ProductRepository;
import com.sparta.product.domain.repository.TimeSaleProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeSaleProductService {

    private final TimeSaleProductRepository timeSaleProductRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Response<Void> createTimeSaleProduct(TimeSaleProductRequestDto timeSaleProductRequestDto, String role) {
        checkIsMaster(role);

        Long productId = timeSaleProductRequestDto.productId();
        Product product = productRepository.findByIdAndIsDeletedFalse(productId).orElseThrow(NotFoundProductException::new);

        // 타임 세일 등록 수량이 상품에 있는 수량보다 적거나 같아야함
        if (timeSaleProductRequestDto.quantity() > product.getStock()) {
            throw new TimeSaleQuantityExceedProductStockException();
        }

        TimeSaleProduct timeSaleProduct = TimeSaleProduct.createOf(timeSaleProductRequestDto, product);
        product.addTimeSaleProductList(timeSaleProduct);

        timeSaleProductRepository.save(timeSaleProduct);
        return new Response<>(HttpStatus.CREATED.value(), "타임 세일 등록 완료", null);
    }

    private void checkIsMaster(String role) {
        if (!role.equals(UserRoleEnum.MASTER.toString())) {
            log.info("forbidden role in checkIsMaster: {}", role);
            throw new ForbiddenRoleException();
        }
    }
}
