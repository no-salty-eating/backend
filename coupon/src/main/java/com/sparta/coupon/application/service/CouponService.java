package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.NOT_FOUND_COUPON;

import com.sparta.coupon.application.dto.request.CouponDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.infrastructure.repository.CouponRepository;
import com.sparta.coupon.model.core.Coupon;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public CouponDto.GetResponse createCoupon(CouponDto.CreateRequest requestDto) {

        Coupon coupon = requestDto.toEntity();
        couponRepository.save(coupon);

        return CouponDto.GetResponse.from(coupon);

    }

    @Transactional(readOnly = true)
    public CouponDto.GetDetailResponse getCoupon(Long id) {

        Coupon coupon = couponRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        return CouponDto.GetDetailResponse.from(coupon);
    }

    @Transactional(readOnly = true)
    public List<CouponDto.GetDetailResponse> getAllCoupons() {

        List<Coupon> coupons =  couponRepository.findByIsDeletedFalse()
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        return  coupons.stream()
                .map(CouponDto.GetDetailResponse::from)
                .collect(Collectors.toList());
    }

}
