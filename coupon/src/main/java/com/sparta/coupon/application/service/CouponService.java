package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.JSON_PROCESSING_ERROR;
import static com.sparta.coupon.application.exception.Error.NOT_FOUND_COUPON;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coupon.application.dto.request.CreateCouponRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponDetailResponseDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.domain.repository.CouponRepository;
import com.sparta.coupon.domain.core.Coupon;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "coupon service")
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private static final String COUPON_INFO_KEY = "coupon:info:";

    @Transactional
    public GetCouponResponseDto createCoupon(CreateCouponRequestDto requestDto) {


        Coupon coupon = Coupon.createCoupon(requestDto.name(), requestDto.discountType(), requestDto.discountValue(), requestDto.minOrderAmount(), requestDto.maxDiscountAmount(), requestDto.totalQuantity(), requestDto.startTime(), requestDto.endTime());
        couponRepository.save(coupon);

        // Redis에 정책 정보 저장
        LocalDateTime now = LocalDateTime.now();

        long expirationTime = Duration.between(now, coupon.getEndTime()).getSeconds();
        // Redis에 쿠폰 정보 저장
        try {
            String couponKey = COUPON_INFO_KEY + coupon.getId();
            String couponJson = objectMapper.writeValueAsString(GetCouponDetailResponseDto.from(coupon));
            RBucket<String> bucket = redissonClient.getBucket(couponKey);
            bucket.set(couponJson);
            bucket.expire(expirationTime, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new CouponException(JSON_PROCESSING_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return GetCouponResponseDto.from(coupon);

    }

    @Transactional(readOnly = true)
    public Coupon getCouponEntity(Long id) {

        String couponKey = COUPON_INFO_KEY + id;
        RBucket<String> bucket = redissonClient.getBucket(couponKey);
        String policyJson = bucket.get();
        if (policyJson != null) {
            try {
                return objectMapper.readValue(policyJson, Coupon.class);
            } catch (JsonProcessingException e) {
                throw new CouponException(JSON_PROCESSING_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        LocalDateTime now = LocalDateTime.now();

        return couponRepository.findByIdAndIsDeletedFalseAndIsPublicTrueAndTimeValid(id, now)
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public GetCouponDetailResponseDto getCoupon(Long id) {
        // 쿠폰 엔티티 가져오기
        Coupon coupon = getCouponEntity(id);
        // GetCouponDetailResponseDto로 변환 후 반환
        return GetCouponDetailResponseDto.from(coupon);
    }

    @Transactional(readOnly = true)
    public List<GetCouponDetailResponseDto> getAllCoupons() {

        LocalDateTime now = LocalDateTime.now();

        List<Coupon> coupons =  couponRepository.findByIsDeletedFalseAndIsPublicTrueTimeValid(now)
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        return  coupons.stream()
                .map(GetCouponDetailResponseDto::from).toList();
    }

}
