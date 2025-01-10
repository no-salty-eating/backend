package com.sparta.coupon.infrastructure.repository;

import com.sparta.coupon.model.core.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {




}