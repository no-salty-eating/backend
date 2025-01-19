package com.sparta.coupon.domain.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sparta.coupon.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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
@Table(name = "tb_coupon")
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("couponId") // JSON의 couponId 필드를 매핑
    private Long id;

    @Column(nullable = false)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountTypeEnum discountType;

    @Column(nullable = false)
    private Integer totalQuantity;

    /*
    @Column(nullable = false)
    private Integer issueQuantity;
     */

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private LocalDateTime expireTime;

    @Column(nullable = false)
    private Integer discountValue;

    @Column(nullable = false)
    private Integer minOrderAmount;

    @Column(nullable = false)
    private Integer maxDiscountAmount;

    // 외부에서는 직접 Coupon.builder()를 사용할 수 없지만, 내부적으로는 객체 생성이 가능
    public static Coupon createCoupon(String name,
                                      DiscountTypeEnum discountType,
                                      Integer discountValue,
                                      Integer minOrderAmount,
                                      Integer maxDiscountAmount,
                                      Integer totalQuantity,
                                      LocalDateTime startTime,
                                      LocalDateTime endTime) {
        return Coupon.builder()
                .name(name)
                .discountType(discountType)
                .discountValue(discountValue)
                .minOrderAmount(minOrderAmount)
                .maxDiscountAmount(maxDiscountAmount)
                .totalQuantity(totalQuantity)
                //.issueQuantity(0)  // 기본적으로 issueQuantity는 0으로 설정
                .startTime(startTime)
                .endTime(endTime)
                .expireTime(endTime.plusDays(30)) // 만료 시간을 종료 시간 기준으로 30일 후로 설정
                .build();
    }

    /*
    public void issueCoupon() {
        this.issueQuantity++;
    }
    */
}
