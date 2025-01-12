package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.NOT_FOUND_ISSUED_COUPON;
import static com.sparta.coupon.application.exception.Error.UNAVAILABLE_COUPON;

import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.infrastructure.repository.UserCouponRepository;
import com.sparta.coupon.model.core.UserCoupon;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;
    private final UserCouponRedisService userCouponRedisService;
    private final IssuedCouponService issuedCouponService;

    @Transactional
    public GetCouponResponseDto issueUserCoupon(String userId, IssueRequestDto requestDto) {
        UserCoupon userCoupon = userCouponRedisService.issueUserCoupon(userId, requestDto);

        issuedCouponService.updateIssuedCoupon(userCouponRepository.findById(userCoupon.getId())
                .orElseThrow(() -> new CouponException(NOT_FOUND_ISSUED_COUPON, HttpStatus.NOT_FOUND)));

        return GetCouponResponseDto.from(userCoupon.getCoupon());

    }

    @Transactional
    public GetCouponResponseDto useCoupon(String userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findByUserIdAndIdWithLock(Long.parseLong(userId), userCouponId)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        userCoupon.use();
        issuedCouponService.updateIssuedCoupon(userCoupon);

        return GetCouponResponseDto.from(userCoupon.getCoupon());
    }

    @Transactional
    public GetCouponResponseDto cancelCoupon(String userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findByUserIdAndIdWithLock(Long.parseLong(userId), userCouponId)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        userCoupon.cancel();
        issuedCouponService.updateIssuedCoupon(userCoupon);

        return GetCouponResponseDto.from(userCoupon.getCoupon());
    }

    @Transactional(readOnly = true)
    public List<GetUserCouponDetailResponseDto> getCouponList(String userId, List<Long> userCouponIds) {

        List<GetUserCouponDetailResponseDto> cachedCoupon = issuedCouponService.getIssuedCoupon(Long.parseLong(userId), userCouponIds);

        if (cachedCoupon != null) {
            return cachedCoupon;
        }

        List<UserCoupon> userCoupons =  userCouponRepository.findByUserIdAndUserCouponIdsAvailable(Long.parseLong(userId), userCouponIds)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        userCoupons.forEach(issuedCouponService::updateIssuedCoupon);

        return  userCoupons.stream()
                .map(GetUserCouponDetailResponseDto::from).toList();
    }

}
