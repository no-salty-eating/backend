package com.sparta.coupon.infrastructure.repository;

import com.sparta.coupon.model.core.Coupon;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByIdAndIsDeletedFalse(Long couponId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)  // @Lock 어노테이션을 쿼리 메소드에 적용
    @Query("SELECT c FROM Coupon c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Coupon> findByIdAndIsDeletedFalseWithLock(@Param("id") Long couponId);


    Optional<List<Coupon>> findByIsDeletedFalse();


}