package com.sparta.coupon.model.core;


import static com.sparta.coupon.application.exception.Error.CANCEL_UNAVAILABLE_COUPON;
import static com.sparta.coupon.application.exception.Error.EXPIRED_COUPON;
import static com.sparta.coupon.application.exception.Error.ISSUE_NOT_VALID_TIME;
import static com.sparta.coupon.application.exception.Error.USED_COUPON;

import com.sparta.coupon.application.exception.CouponException;
import com.sparta.coupon.model.CouponStatusEnum;
import com.sparta.coupon.model.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Table(name = "tb_user_coupon")
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatusEnum couponStatus;

    @Column(nullable = false)
    private LocalDateTime usedAt;

    public static UserCoupon issueUserCoupon(Long userId, Coupon coupon) {
        return UserCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .couponStatus(CouponStatusEnum.AVAILABLE)
                .build();
    }

    public void use() {
        if (couponStatus == CouponStatusEnum.USED) {
            throw new CouponException(USED_COUPON, HttpStatus.BAD_REQUEST);
        }
        if (isExpired()) {
            this.couponStatus = CouponStatusEnum.EXPIRED;
            throw new CouponException(EXPIRED_COUPON, HttpStatus.BAD_REQUEST);
        }
        this.couponStatus = CouponStatusEnum.USED;
        this.usedAt = LocalDateTime.now();
    }

    public void cancel() {
        if (couponStatus != CouponStatusEnum.USED) {
            throw new CouponException(CANCEL_UNAVAILABLE_COUPON, HttpStatus.BAD_REQUEST);
        }
        this.couponStatus = CouponStatusEnum.AVAILABLE;
        this.usedAt = null;
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(coupon.getStartTime()) || now.isAfter(coupon.getEndTime());
    }

}
