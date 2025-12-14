package com.hyperativatechtest.features.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardLookupResponse {
    private UUID id;
    private String maskedCardNumber;
}

