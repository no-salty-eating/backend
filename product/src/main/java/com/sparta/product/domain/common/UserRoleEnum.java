package com.sparta.product.domain.common;

public enum UserRoleEnum {

    MASTER(Authority.MASTER),
    SELLER(Authority.SELLER),
    USER(Authority.USER);

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String MASTER = "MASTER";
        public static final String SELLER = "SELLER";
        public static final String USER = "USER";
    }
}