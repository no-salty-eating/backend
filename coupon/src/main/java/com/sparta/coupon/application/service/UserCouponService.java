package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.COUPON_EXHAUSTED;
import static com.sparta.coupon.application.exception.Error.ISSUE_NOT_VALID_TIME;
import static com.sparta.coupon.application.exception.Error.NOT_FOUND_COUPON;

import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.infrastructure.repository.CouponRepository;
import com.sparta.coupon.infrastructure.repository.UserCouponRepository;
import com.sparta.coupon.model.core.Coupon;
import com.sparta.coupon.model.core.UserCoupon;
import java.time.LocalDateTime;
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
    public GetUserCouponDetailResponseDto issueUserCoupon(String id, IssueRequestDto requestDto) {

         Coupon coupon = couponRepository.findByIdAndIsDeletedFalseWithLock(requestDto.couponId())
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
            throw new CouponException(ISSUE_NOT_VALID_TIME, HttpStatus.BAD_REQUEST);
        }

        if (coupon.getIssueQuantity().equals(coupon.getTotalQuantity())) {
            throw new CouponException(COUPON_EXHAUSTED, HttpStatus.BAD_REQUEST);
        }

        UserCoupon userCoupon = UserCoupon.issueUserCoupon(Long.parseLong(id), coupon);
        userCouponRepository.save(userCoupon);

        return GetUserCouponDetailResponseDto.from(userCoupon);

    }

}
