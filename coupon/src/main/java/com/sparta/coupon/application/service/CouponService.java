package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.NOT_FOUND_COUPON;

import com.sparta.coupon.application.dto.request.CreateCouponRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponDetailResponseDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
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
    public GetCouponResponseDto createCoupon(CreateCouponRequestDto requestDto) {

        Coupon coupon = requestDto.toEntity();
        couponRepository.save(coupon);

        return GetCouponResponseDto.from(coupon);

    }

    @Transactional(readOnly = true)
    public GetCouponDetailResponseDto getCoupon(Long id) {

        Coupon coupon = couponRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        return GetCouponDetailResponseDto.from(coupon);
    }

    @Transactional(readOnly = true)
    public List<GetCouponDetailResponseDto> getAllCoupons() {

        List<Coupon> coupons =  couponRepository.findByIsDeletedFalse()
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        return  coupons.stream()
                .map(GetCouponDetailResponseDto::from)
                .collect(Collectors.toList());
    }

}
