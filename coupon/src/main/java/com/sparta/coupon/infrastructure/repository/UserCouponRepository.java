package com.sparta.coupon.infrastructure.repository;

import com.sparta.coupon.model.core.UserCoupon;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Query("SELECT uc FROM UserCoupon uc WHERE uc.userId = :userId AND uc.id IN :userCouponIds AND uc.isDeleted = false AND uc.isPublic = true AND uc.couponStatus = 'AVAILABLE'")
    Optional<List<UserCoupon>> findByUserIdAndUserCouponIdsAvailable(@Param("userId") Long userId, @Param("userCouponIds") List<Long> userCouponIds);

    /**
     * PESSIMISTIC_WRITE 를 사용하는 이유는 데이터의 일관성을 보장하기 위함
     * 동시에 여러 트랜잭션이 동일한 데이터를 수정하려고 할 때 충돌을 방지하고, 데이터 무결성을 유지하기 위해 사용
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.id = :id AND uc.userId = :userId AND uc.isDeleted = false AND uc.isPublic = true")
    Optional<UserCoupon> findByUserIdAndIdWithLock(@Param("userId") Long userId, @Param("id") Long id);
}