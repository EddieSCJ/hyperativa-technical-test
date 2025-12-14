package com.hyperativatechtest.features.card.model;

import lombok.Getter;

@Getter
public enum CardJobStatusEnum {
    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    COMPLETED("COMPLETED"),
    FAILED("FAILED");

    private final String value;

    CardJobStatusEnum(String value) {
        this.value = value;
    }

}

