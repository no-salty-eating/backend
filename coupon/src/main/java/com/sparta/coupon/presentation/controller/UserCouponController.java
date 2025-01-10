package com.sparta.coupon.presentation.controller;

import com.sparta.coupon.application.dto.Response;
import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("userCoupons")
public class UserCouponController {

    private final UserCouponService userCouponService;

    @PostMapping()
    public Response<GetUserCouponDetailResponseDto> issueUserCoupon(@RequestHeader(value = "X-Id") String requestId,
                                                                          @RequestBody IssueRequestDto request) {
        return new Response<>(HttpStatus.CREATED.value(), "쿠폰 발급 완료", userCouponService.issueUserCoupon(requestId, request));
    }

    @PostMapping("/use/{userCouponId}")
    public Response<GetCouponResponseDto> useCoupon(@RequestHeader(value = "X-Id") String requestId,
            @PathVariable Long userCouponId) {
        return new Response<>(HttpStatus.OK.value(), "쿠폰 사용 완료", userCouponService.useCoupon(requestId, userCouponId));
    }

    @PostMapping("/cancel/{userCouponId}")
    public Response<GetCouponResponseDto> cancelCoupon(@RequestHeader(value = "X-Id") String requestId,
                                                    @PathVariable Long userCouponId) {
        return new Response<>(HttpStatus.OK.value(), "쿠폰 취소 완료", userCouponService.cancelCoupon(requestId, userCouponId));
    }

}
