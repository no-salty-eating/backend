package com.sparta.user.domain.core;

import com.sparta.user.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "tb_user")
public class User extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "login_id", unique = true, nullable = false)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRoleEnum role;

    /**
     * Static factory method for creating a User instance.
     */


    public static User createUser(String loginId, String password, String name, String email, String role) {
        return User.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .email(email)
                .role(UserRoleEnum.valueOf(role))  // role은 문자열로 받아서 UserRoleEnum으로 변환
                .build();
    }
    public void updateUser(String password, String name, String email, Boolean isPublic) {
        if (password != null) {
            this.password = password;
        }
        if (name != null) {
            this.name = name;
        }
        if (email != null) {
            this.email = email;
        }
        if (isPublic != null) {
            if (isPublic) {
                this.toPublic();
            } else {
                this.toPrivate();
            }
        }
    }
}
