package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.COUPON_EXHAUSTED;
import static com.sparta.coupon.application.exception.Error.ISSUE_NOT_VALID_TIME;
import static com.sparta.coupon.application.exception.Error.NOT_FOUND_COUPON;
import static com.sparta.coupon.application.exception.Error.UNAVAILABLE_COUPON;

import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.infrastructure.repository.CouponRepository;
import com.sparta.coupon.infrastructure.repository.UserCouponRepository;
import com.sparta.coupon.model.core.Coupon;
import com.sparta.coupon.model.core.UserCoupon;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    @Transactional
    public GetCouponResponseDto issueUserCoupon(String userId, IssueRequestDto requestDto) {

         Coupon coupon = couponRepository.findByIdAndIsDeletedFalseWithLock(requestDto.couponId())
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new CouponException(ISSUE_NOT_VALID_TIME, HttpStatus.BAD_REQUEST);
        }

        if (coupon.getIssueQuantity().equals(coupon.getTotalQuantity())) {
            throw new CouponException(COUPON_EXHAUSTED, HttpStatus.BAD_REQUEST);
        }

        UserCoupon userCoupon = UserCoupon.issueUserCoupon(Long.parseLong(userId), coupon);
        userCoupon.getCoupon().issueCoupon();
        userCouponRepository.save(userCoupon);

        return GetCouponResponseDto.from(userCoupon.getCoupon());

    }

    @Transactional
    public GetCouponResponseDto useCoupon(String userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findByIdAndUserIdAndIsDeletedFalse(Long.parseLong(userId), userCouponId)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        userCoupon.use();

        return GetCouponResponseDto.from(userCoupon.getCoupon());
    }

    @Transactional
    public GetCouponResponseDto cancelCoupon(String userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findByIdAndUserIdAndIsDeletedFalse(Long.parseLong(userId), userCouponId)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        userCoupon.cancel();

        return GetCouponResponseDto.from(userCoupon.getCoupon());
    }

    @Transactional(readOnly = true)
    public List<GetUserCouponDetailResponseDto> getCouponList(String userId, List<Long> userCouponIds) {


        List<UserCoupon> userCoupons =  userCouponRepository.findByUserIdAndUserCouponIdsAvailable(Long.parseLong(userId), userCouponIds)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        return  userCoupons.stream()
                .map(GetUserCouponDetailResponseDto::from).toList();
    }

}
