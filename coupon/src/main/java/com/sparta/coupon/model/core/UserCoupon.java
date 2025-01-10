package com.sparta.coupon.model.core;


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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public static UserCoupon issueUserCoupon(Long userId, Coupon coupon) {
        return UserCoupon.builder()
                .userId(userId)
                .coupon(coupon)
                .couponStatus(CouponStatusEnum.AVAILABLE)
                .build();
    }
}
