package com.hyperativatechtest.features.card.dto.simple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponse {

    private UUID id;
    private String maskedCardNumber;
    private OffsetDateTime createdAt;
}

