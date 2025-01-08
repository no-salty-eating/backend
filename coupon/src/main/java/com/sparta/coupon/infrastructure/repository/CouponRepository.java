package com.sparta.coupon.infrastructure.repository;

import com.sparta.coupon.model.core.Coupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByIdAndIsDeletedFalse(Long couponId);

    Optional<List<Coupon>> findByIsDeletedFalse();
}