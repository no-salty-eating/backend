package com.sparta.coupon.infrastructure.repository;

import com.sparta.coupon.model.core.UserCoupon;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    Optional<UserCoupon> findByIdAndUserIdAndIsDeletedFalse(Long userId, Long id);

    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.id IN :userCouponIds AND uc.isDeleted = false AND uc.couponStatus = 'AVAILABLE'")
    Optional<List<UserCoupon>> findByUserIdAndUserCouponIdsAvailable(@Param("userId") Long userId, @Param("userCouponIds") List<Long> userCouponIds);

}