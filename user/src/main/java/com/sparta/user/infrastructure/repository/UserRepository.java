package com.sparta.user.infrastructure.repository;

import com.sparta.user.domain.model.core.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByLoginIdAndIsDeletedFalse(String loginId);

    Optional<User> findByEmail(String email);
}
