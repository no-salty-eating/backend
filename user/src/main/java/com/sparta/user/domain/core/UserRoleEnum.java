package com.sparta.user.domain.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {

    USER(Authority.USER),
    SELLER(Authority.SELLER),
    MASTER(Authority.MASTER);


    private final String authority;

    public static class Authority {

        private static final String USER = "ROLE_USER";
        private static final String SELLER = "ROLE_SELLER";
        private static final String MASTER = "ROLE_MASTER";
    }
}