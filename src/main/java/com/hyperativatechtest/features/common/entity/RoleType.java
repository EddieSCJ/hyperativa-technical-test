package com.hyperativatechtest.features.common.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


@Getter
public enum RoleType {
    ADMIN("ADMIN", "Administrator with full system access",
        Permission.USER_CREATE, Permission.USER_READ, Permission.USER_UPDATE, Permission.USER_DELETE,
        Permission.CARD_CREATE, Permission.CARD_READ, Permission.CARD_UPDATE, Permission.CARD_DELETE),

    USER("USER", "Regular user with standard permissions",
        Permission.CARD_CREATE, Permission.CARD_READ);

    private final String name;
    private final String description;
    private final Set<Permission> permissions;

    RoleType(String name, String description, Permission... permissions) {
        this.name = name;
        this.description = description;
        this.permissions = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(permissions)));
    }
}

