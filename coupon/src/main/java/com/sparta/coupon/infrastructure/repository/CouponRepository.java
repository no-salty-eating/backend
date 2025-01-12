package com.sparta.coupon.infrastructure.repository;

import com.sparta.coupon.model.core.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon c WHERE c.id = :couponId AND c.isDeleted = false AND c.isPublic = true " +
            "AND c.startTime <= :now AND c.endTime >= :now")
    Optional<Coupon> findByIdAndIsDeletedFalseAndIsPublicTrueAndTimeValid(@Param("couponId") Long couponId, @Param("now") LocalDateTime now);

    Optional<List<Coupon>> findByIsDeletedFalseAndIsPublicTrue();


}