package com.hyperativatechtest.features.common.logging;

import lombok.Getter;

@Getter
public enum EntityType {
    CARD("CARD"),
    AUTH("AUTH"),
    FILE("FILE"),
    OTHER("OTHER");

    private final String value;

    EntityType(String value) {
        this.value = value;
    }

    public static EntityType fromUri(String uri) {
        if (uri.contains("/cards")) {
            return CARD;
        } else if (uri.contains("/auth")) {
            return AUTH;
        } else if (uri.contains("/upload")) {
            return FILE;
        }
        return OTHER;
    }
}

