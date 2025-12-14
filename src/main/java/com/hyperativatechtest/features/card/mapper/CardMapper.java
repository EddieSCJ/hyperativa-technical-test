package com.hyperativatechtest.features.card.mapper;

import com.hyperativatechtest.features.card.dto.simple.CardLookupResponse;
import com.hyperativatechtest.features.card.dto.simple.CardResponse;
import com.hyperativatechtest.features.card.entity.Card;
import com.hyperativatechtest.features.common.crypto.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardMapper {

    private final EncryptionService encryptionService;

    public CardResponse toResponse(Card card, String originalCardNumber) {
        return CardResponse.builder()
                .id(card.getId())
                .maskedCardNumber(encryptionService.mask(originalCardNumber))
                .createdAt(card.getCreatedAt())
                .build();
    }

    public CardLookupResponse toLookupResponse(Card card, String originalCardNumber) {
        return CardLookupResponse.builder()
                .id(card.getId())
                .maskedCardNumber(encryptionService.mask(originalCardNumber))
                .build();
    }
}

