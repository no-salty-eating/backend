package com.sparta.coupon.presentation.controller;

import com.sparta.coupon.application.dto.Response;
import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("userCoupons")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping()
    public Response<GetCouponResponseDto> issueUserCoupon(@RequestHeader(name = "X-Id") Long userId, @RequestBody IssueRequestDto request) {
        return new Response<>(HttpStatus.CREATED.value(), "쿠폰 발급 완료", userCouponService.issueUserCoupon(userId, request));
    }

    @PostMapping("/use/{userCouponId}")
    public Response<GetCouponResponseDto> useCoupon(@RequestHeader(name = "X-Id") Long userId, @PathVariable Long userCouponId) {
        return new Response<>(HttpStatus.OK.value(), "쿠폰 사용 완료", userCouponService.useCoupon(userId, userCouponId));
    }

    @PostMapping("/cancel/{userCouponId}")
    public Response<GetCouponResponseDto> cancelCoupon(@RequestHeader(name = "X-Id") Long userId, @PathVariable Long userCouponId) {
        return new Response<>(HttpStatus.OK.value(), "쿠폰 취소 완료", userCouponService.cancelCoupon(userId, userCouponId));
    }

    @GetMapping
    public Response<List<GetUserCouponDetailResponseDto>> getUserCoupons(@RequestHeader(name = "X-Id") Long userId, @RequestParam(required = false) List<Long> userCouponIds) {

        return new Response<>(HttpStatus.OK.value(), "사용 가능한 쿠폰 조회 완료", userCouponService.getCouponList(userId, userCouponIds));
    }

    @GetMapping("/list/{userId}")
    public List<GetUserCouponDetailResponseDto> getCouponList(@PathVariable Long userId, @RequestParam(required = false) List<Long> userCouponIds) {
        return userCouponService.getCouponList(userId, userCouponIds);
    }

}

