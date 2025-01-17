package com.sparta.coupon.application.service;


import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.domain.core.UserCoupon;
import com.sparta.coupon.domain.repository.UserCouponRepository;
import com.sparta.coupon.infrastructure.config.UserIdInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.sparta.coupon.application.exception.Error.UNAVAILABLE_COUPON;

@Service
@RequiredArgsConstructor
public class UserCouponService {

	private final UserCouponRepository userCouponRepository;
	private final UserCouponRedisService userCouponRedisService;

	@Transactional
	public GetCouponResponseDto issueUserCoupon(IssueRequestDto requestDto) {
		UserCoupon userCoupon = userCouponRedisService.issueUserCoupon(UserIdInterceptor.getCurrentUserId(), requestDto);

		return GetCouponResponseDto.from(userCoupon.getCoupon());

	}

	@Transactional
	public GetCouponResponseDto useCoupon(Long userId, Long userCouponId) {

		UserCoupon userCoupon = userCouponRepository.findByUserIdAndIdWithLock(userId, userCouponId)
				.orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

		userCoupon.use();

		return GetCouponResponseDto.from(userCoupon.getCoupon());
	}

	@Transactional
	public GetCouponResponseDto cancelCoupon(Long userCouponId) {

		UserCoupon userCoupon = userCouponRepository.findByUserIdAndIdWithLock(UserIdInterceptor.getCurrentUserId(), userCouponId)
				.orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

		userCoupon.cancel();

		return GetCouponResponseDto.from(userCoupon.getCoupon());
	}


	@Transactional(readOnly = true)
	public List<GetUserCouponDetailResponseDto> getCouponList(List<Long> userCouponIds, Long userId) {
		List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndUserCouponIdsAvailable(userId, userCouponIds)
				.orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

		return userCoupons.stream()
				.map(GetUserCouponDetailResponseDto::from).toList();
	}

}
