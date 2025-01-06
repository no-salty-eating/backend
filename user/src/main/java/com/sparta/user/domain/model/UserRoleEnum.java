package com.sparta.user.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRoleEnum {

    CUSTOMER(Authority.CUSTOMER),
    SELLER(Authority.SELLER),
    ADMIN(Authority.ADMIN);


    private final String authority;

    public static class Authority {

        private static String CUSTOMER = "ROLE_CUSTOMER";
        private static String SELLER = "ROLE_SELLER";
        private static String ADMIN = "ROLE_ADMIN";
    }
}