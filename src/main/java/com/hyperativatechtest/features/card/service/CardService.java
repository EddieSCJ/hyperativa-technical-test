package com.hyperativatechtest.features.card.service;

import com.hyperativatechtest.features.card.dto.simple.CardLookupResponse;
import com.hyperativatechtest.features.card.dto.simple.CardRequest;
import com.hyperativatechtest.features.card.dto.simple.CardResponse;
import com.hyperativatechtest.features.card.entity.Card;
import com.hyperativatechtest.features.card.exception.CardAlreadyExistsException;
import com.hyperativatechtest.features.card.exception.CardNotFoundException;
import com.hyperativatechtest.features.card.mapper.CardMapper;
import com.hyperativatechtest.features.card.repository.CardRepository;
import com.hyperativatechtest.features.common.crypto.EncryptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

    private final CardRepository cardRepository;
    private final EncryptionService encryptionService;
    private final CardMapper cardMapper;

    @Transactional
    public CardResponse createCard(CardRequest request) {
        String cardNumber = request.getCardNumber().replaceAll("\\s", "");
        String cardHash = encryptionService.hash(cardNumber);

        if (cardRepository.existsByCardHash(cardHash)) {
            log.warn("Attempted to create duplicate card");
            throw new CardAlreadyExistsException("Card already exists in the system");
        }

        String encryptedCardNumber = encryptionService.encrypt(cardNumber);
        String username = getCurrentUsername();

        Card card = Card.builder()
                .encryptedCardNumber(encryptedCardNumber)
                .cardHash(cardHash)
                .lotId(null)
                .sequenceNumber(null)
                .createdBy(username)
                .build();

        Card savedCard = cardRepository.save(card);
        log.info("Card created successfully with id: {}", savedCard.getId());

        return cardMapper.toResponse(savedCard, cardNumber);
    }

    @Transactional(readOnly = true)
    public CardLookupResponse lookupCard(String cardNumber) {
        cardNumber = cardNumber.replaceAll("\\s", "");
        String cardHash = encryptionService.hash(cardNumber);

        Card card = cardRepository.findByCardHash(cardHash)
                .orElseThrow(() -> {
                    log.debug("Card not found for hash: {}", cardHash);
                    return new CardNotFoundException("Card not found");
                });

        log.debug("Card found with id: {}", card.getId());
        return cardMapper.toLookupResponse(card, cardNumber);
    }


    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}

