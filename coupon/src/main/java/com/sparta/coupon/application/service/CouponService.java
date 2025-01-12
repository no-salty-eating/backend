package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.JSON_PROCESSING_ERROR;
import static com.sparta.coupon.application.exception.Error.NOT_FOUND_COUPON;
import static com.sparta.coupon.application.exception.Error.NOT_VALID_END_TIME;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coupon.application.dto.request.CreateCouponRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponDetailResponseDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.infrastructure.repository.CouponRepository;
import com.sparta.coupon.model.core.Coupon;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
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

    private static final String COUPON_QUANTITY_KEY = "coupon:quantity:";
    private static final String COUPON_KEY = "coupon:info:";

    @Transactional
    public GetCouponResponseDto createCoupon(CreateCouponRequestDto requestDto) {

        Coupon coupon = requestDto.toEntity();

        if(!coupon.getStartTime().isBefore(coupon.getEndTime())) {
            throw new CouponException(NOT_VALID_END_TIME, HttpStatus.BAD_REQUEST);
        }

        Coupon createCoupon = couponRepository.save(coupon);

        return GetCouponResponseDto.from(createCoupon);

    }

    @Transactional(readOnly = true)
    public Coupon getCouponEntity(Long id) {
        // Redis에서 쿠폰 정보 가져오기
        String couponKey = COUPON_KEY + id;
        RBucket<String> bucket = redissonClient.getBucket(couponKey);
        String couponJson = bucket.get();
        if (couponJson != null) {
            try {
                return objectMapper.readValue(couponJson, Coupon.class);
            } catch (JsonProcessingException e) {
                throw new CouponException(JSON_PROCESSING_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // 데이터베이스에서 쿠폰 정보 가져오기

        LocalDateTime now = LocalDateTime.now();

        Coupon coupon = couponRepository.findByIdAndIsDeletedFalseAndIsPublicTrueAndTimeValid(id, now)
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        long expirationTime = Duration.between(now, coupon.getEndTime()).getSeconds();
        // Redis에 쿠폰 정보 저장
        try {
            couponJson = objectMapper.writeValueAsString(coupon);
            bucket.set(couponJson);
            bucket.expire(expirationTime, TimeUnit.SECONDS);

            // Redis에 수량 설정
            String quantityKey = COUPON_QUANTITY_KEY + id;
            RAtomicLong atomicQuantity = redissonClient.getAtomicLong(quantityKey);
            atomicQuantity.set(coupon.getIssueQuantity());
        } catch (JsonProcessingException e) {
            throw new CouponException(JSON_PROCESSING_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return coupon;
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

        List<Coupon> coupons =  couponRepository.findByIsDeletedFalseAndIsPublicTrue ()
                .orElseThrow(() -> new CouponException(NOT_FOUND_COUPON, HttpStatus.NOT_FOUND));

        return  coupons.stream()
                .map(GetCouponDetailResponseDto::from).toList();
    }

}
