package com.hyperativatechtest.features.common.entity;

import lombok.Getter;

@Getter
public enum Permission {
    USER_CREATE("user:create", "Create new users"),
    USER_READ("user:read", "View user information"),
    USER_UPDATE("user:update", "Update user information"),
    USER_DELETE("user:delete", "Delete users"),

    CARD_CREATE("card:create", "Create/upload cards"),
    CARD_READ("card:read", "View cards"),
    CARD_UPDATE("card:update", "Update cards"),
    CARD_DELETE("card:delete", "Delete cards");

    private final String code;
    private final String description;

    Permission(String code, String description) {
        this.code = code;
        this.description = description;
    }

}

