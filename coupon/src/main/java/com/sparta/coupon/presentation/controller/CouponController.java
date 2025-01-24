package com.sparta.coupon.presentation.controller;

import com.sparta.coupon.application.dto.Response;
import com.sparta.coupon.application.dto.request.CreateCouponRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponDetailResponseDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.service.CouponService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public Response<GetCouponResponseDto> createCoupon(@Valid @RequestBody CreateCouponRequestDto request) {

        return new Response<>(HttpStatus.CREATED.value(), "쿠폰 생성 완료", couponService.createCoupon(request));
    }

    @GetMapping("/{couponId}")
    public Response<GetCouponDetailResponseDto> getCoupon(@PathVariable Long couponId) {

        return new Response<>(HttpStatus.CREATED.value(), "쿠폰 단건 조회 완료", couponService.getCoupon(couponId));
    }

    @GetMapping
    public Response<List<GetCouponDetailResponseDto>> getAllCoupons() {

        return new Response<>(HttpStatus.CREATED.value(), "쿠폰 전체 조회 완료", couponService.getAllCoupons());
    }


}
