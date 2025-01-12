package com.sparta.coupon.application.service;

import static com.sparta.coupon.application.exception.Error.COUPON_EXHAUSTED;
import static com.sparta.coupon.application.exception.Error.INTERNAL_SERVER_ERROR;
import static com.sparta.coupon.application.exception.Error.ISSUE_COUPON_LATER;
import static com.sparta.coupon.application.exception.Error.ISSUE_NOT_VALID_TIME;

import com.sparta.coupon.application.dto.request.IssueRequestDto;
import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.infrastructure.repository.UserCouponRepository;
import com.sparta.coupon.model.core.Coupon;
import com.sparta.coupon.model.core.UserCoupon;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j(topic = "coupon redis service")
@Service
@RequiredArgsConstructor
public class UserCouponRedisService {

    private final RedissonClient redissonClient;
    private final UserCouponRepository userCouponRepository;
    private final CouponService couponService;

    private static final String COUPON_QUANTITY_KEY = "coupon:quantity:";
    private static final String COUPON_LOCK_KEY = "coupon:lock:";
    private static final long LOCK_WAIT_TIME = 3;
    private static final long LOCK_LEASE_TIME = 5;

    @Transactional
    public UserCoupon issueUserCoupon(String userId, IssueRequestDto requestDto) {
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

            UserCoupon userCoupon = UserCoupon.issueUserCoupon(Long.parseLong(userId), coupon);
            userCoupon.getCoupon().issueCoupon();
            return userCouponRepository.save(userCoupon);


        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CouponException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
