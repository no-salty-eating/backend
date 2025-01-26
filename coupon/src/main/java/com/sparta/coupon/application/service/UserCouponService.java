package com.sparta.coupon.application.service;


import static com.sparta.coupon.application.exception.Error.COUPON_EXHAUSTED;
import static com.sparta.coupon.application.exception.Error.INTERNAL_SERVER_ERROR;
import static com.sparta.coupon.application.exception.Error.ISSUE_COUPON_LATER;
import static com.sparta.coupon.application.exception.Error.ISSUE_NOT_VALID_TIME;
import static com.sparta.coupon.application.exception.Error.UNAVAILABLE_COUPON;

import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.dto.response.GetCouponResponseDto;
import com.sparta.coupon.application.dto.response.GetUserCouponDetailResponseDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.domain.core.Coupon;
import com.sparta.coupon.domain.core.UserCoupon;
import com.sparta.coupon.domain.repository.UserCouponRepository;
import com.sparta.coupon.infrastructure.kafka.CouponProducer;
import com.sparta.coupon.infrastructure.kafka.event.IssueCouponMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCouponService {

    private final UserCouponRepository userCouponRepository;


    private final RedissonClient redissonClient;
    private final CouponService couponService;
    private final CouponProducer couponProducer;

    private static final String COUPON_QUANTITY_KEY = "coupon:quantity:";
    private static final String COUPON_LOCK_KEY = "coupon:lock:";
    private static final long LOCK_WAIT_TIME = 3;
    private static final long LOCK_LEASE_TIME = 5;


    @Transactional(readOnly = true)
    public GetCouponResponseDto requestIssueUserCoupon(Long userId, IssueRequestDto requestDto) {
        String quantityKey = COUPON_QUANTITY_KEY + requestDto.couponId();
        String lockKey = COUPON_LOCK_KEY + requestDto.couponId();
        RLock lock = redissonClient.getLock(lockKey);

        try {
            boolean isLocked = lock.tryLock(LOCK_WAIT_TIME, LOCK_LEASE_TIME, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new CouponException(ISSUE_COUPON_LATER, HttpStatus.TOO_MANY_REQUESTS);
            }

            Coupon coupon = couponService.getCouponEntity(requestDto.couponId());

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime())) {
                throw new CouponException(ISSUE_NOT_VALID_TIME, HttpStatus.BAD_REQUEST);
            }

            // 수량 체크 및 감소
            RAtomicLong atomicQuantity = redissonClient.getAtomicLong(quantityKey);
            long issueQuantity = atomicQuantity.incrementAndGet();

            if (issueQuantity > coupon.getTotalQuantity()) {
                atomicQuantity.decrementAndGet();
                throw new CouponException(COUPON_EXHAUSTED, HttpStatus.BAD_REQUEST);
            }

            // Kafka로 쿠폰 발급 요청 전송
            couponProducer.sendCouponIssueRequest(
                    IssueCouponMessage.builder()
                            .couponId(requestDto.couponId())
                            .userId(userId)
                            .build()
            );

            return GetCouponResponseDto.from(coupon);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CouponException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public void issueUserCoupon(IssueCouponMessage requestDto) {
        try {
            Coupon coupon =  couponService.getCouponEntity(requestDto.couponId());

            UserCoupon userCoupon = UserCoupon.issueUserCoupon(requestDto.userId(), coupon);
            userCouponRepository.save(userCoupon);

            log.info("Coupon issued successfully: policyId={}, userId={}", requestDto.couponId(), requestDto.userId());

        } catch (Exception e) {
            log.error("Failed to issue coupon: {}", e.getMessage());
            throw e;
        }

    }

    @Transactional
    public GetCouponResponseDto useCoupon(Long userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findByUserIdAndIdWithLock(userId, userCouponId)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        userCoupon.use();

        return GetCouponResponseDto.from(userCoupon.getCoupon());
    }

    @Transactional
    public GetCouponResponseDto cancelCoupon(Long userId, Long userCouponId) {

        UserCoupon userCoupon = userCouponRepository.findByUserIdAndIdWithLock(userId, userCouponId)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        userCoupon.cancel();

        return GetCouponResponseDto.from(userCoupon.getCoupon());
    }


    @Transactional(readOnly = true)
    public List<GetUserCouponDetailResponseDto> getCouponList(Long userId, List<Long> userCouponIds) {
        List<UserCoupon> userCoupons = userCouponRepository.findByUserIdAndUserCouponIdsAvailable(userId, userCouponIds)
                .orElseThrow(() -> new CouponException(UNAVAILABLE_COUPON, HttpStatus.NOT_FOUND));

        return userCoupons.stream()
                .map(GetUserCouponDetailResponseDto::from).toList();
    }

}
