package com.sparta.user.domain.model.core;

import com.sparta.user.application.dto.request.SignInRequestDto;
import com.sparta.user.application.dto.request.UpdateUserRequestDto;
import com.sparta.user.domain.model.UserRoleEnum;
import com.sparta.user.domain.model.common.BaseEntity;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
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


    public static User createUser(SignInRequestDto dto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .loginId(dto.getLoginId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .role(UserRoleEnum.valueOf(dto.getRole()))
                .build();
    }

    public void updateUser(UpdateUserRequestDto dto, PasswordEncoder passwordEncoder) {
        if (dto.getPassword() != null) {
            this.password = passwordEncoder.encode(dto.getPassword());
        }
        if (dto.getName() != null) {
            this.name = dto.getName();
        }
        if (dto.getEmail() != null) {
            this.email = dto.getEmail();
        }
        if (dto.getIsPublic() != null) {
            if(dto.getIsPublic()) {
                this.toPublic();
            }
            else{
                this.toPrivate();
            }
        }
    }
}
