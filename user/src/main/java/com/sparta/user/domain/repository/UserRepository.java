package com.sparta.user.domain.repository;

import com.sparta.user.domain.core.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByLoginIdAndIsDeletedFalseAndIsPublicTrue(String loginId);

    Optional<User> findByEmail(String email);
}
