package com.hyperativatechtest.features.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardLookupResponse {
    private String id;
    private String maskedCardNumber;
}

