package com.sparta.coupon.infrastructure.repository;

import com.sparta.coupon.model.core.UserCoupon;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByIdAndUserIdAndIsDeletedFalse(Long id, Long userId);

}