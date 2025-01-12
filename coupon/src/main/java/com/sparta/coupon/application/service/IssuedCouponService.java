package com.sparta.coupon.application.service;

import static com.sparta.coupon.application.exception.Error.FOUND_ISSUED_COUPON_ERROR;
import static com.sparta.coupon.application.exception.Error.UPDATE_ISSUED_COUPON_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.model.core.UserCoupon;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j(topic = "coupon state service")
@Service
@RequiredArgsConstructor
public class IssuedCouponService {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private static final String USER_COUPON_KEY = "coupon:issued:";

    /**
     * 쿠폰 상태를 Redis에 저장
     * @param userCoupon 상태를 저장할 쿠폰
     */
    public void updateIssuedCoupon(UserCoupon userCoupon) {
        try {
            String stateKey = USER_COUPON_KEY + userCoupon.getId();
            String couponJson = objectMapper.writeValueAsString(GetUserCouponDetailResponseDto.from(userCoupon));
            RBucket<String> bucket = redissonClient.getBucket(stateKey);
            bucket.set(couponJson);

        } catch (Exception e) {

            throw new CouponException(UPDATE_ISSUED_COUPON_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 쿠폰 상태를 Redis에서 가져옴
     * @param userCouponIds 상태를 가져올 쿠폰 ID
     * @return 쿠폰 상태, 없으면 null
     */
    public List<GetUserCouponDetailResponseDto> getIssuedCoupon(Long userId, List<Long> userCouponIds) {

        List<GetUserCouponDetailResponseDto> couponDetails = new ArrayList<>();

        try {
            for (Long userCouponId : userCouponIds) {
                String stateKey = USER_COUPON_KEY + userCouponId;
                RBucket<String> bucket = redissonClient.getBucket(stateKey);
                String couponJson = bucket.get();

                if (couponJson != null) {
                    GetUserCouponDetailResponseDto detail = objectMapper.readValue(couponJson, GetUserCouponDetailResponseDto.class);
                    if (detail.userId().equals(userId)) {
                        couponDetails.add(detail);
                    }
                }
            }
        } catch (Exception e) {
            throw new CouponException(FOUND_ISSUED_COUPON_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return couponDetails;
    }
}
