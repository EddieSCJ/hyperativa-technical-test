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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardService Tests")
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private EncryptionService encryptionService;

    @Mock
    private CardMapper cardMapper;

    @Mock(lenient = true)
    private SecurityContext securityContext;

    @Mock(lenient = true)
    private Authentication authentication;

    @InjectMocks
    private CardService cardService;

    private CardRequest cardRequest;
    private Card card;
    private CardResponse cardResponse;
    private CardLookupResponse lookupResponse;

    @BeforeEach
    void setUp() {
        cardRequest = CardRequest.builder()
                .cardNumber("4456897999999999")
                .build();

        card = Card.builder()
                .id(UUID.randomUUID())
                .encryptedCardNumber("encrypted123")
                .cardHash("hash123")
                .createdBy("testuser")
                .createdAt(OffsetDateTime.now())
                .build();

        cardResponse = CardResponse.builder()
                .id(UUID.randomUUID())
                .maskedCardNumber("445689****9999")
                .createdAt(OffsetDateTime.now())
                .build();

        lookupResponse = CardLookupResponse.builder()
                .id(UUID.randomUUID())
                .maskedCardNumber("445689****9999")
                .build();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
    }

    @Test
    @DisplayName("Should create card successfully")
    void shouldCreateCardSuccessfully() {
        when(encryptionService.hash("4456897999999999")).thenReturn("hash123");
        when(cardRepository.existsByCardHash("hash123")).thenReturn(false);
        when(encryptionService.encrypt("4456897999999999")).thenReturn("encrypted123");
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(any(Card.class), eq("4456897999999999"))).thenReturn(cardResponse);

        CardResponse result = cardService.createCard(cardRequest);

        assertNotNull(result);
        verify(cardRepository).existsByCardHash("hash123");
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toResponse(any(Card.class), eq("4456897999999999"));
    }

    @Test
    @DisplayName("Should throw exception when card already exists")
    void shouldThrowExceptionWhenCardAlreadyExists() {
        when(encryptionService.hash("4456897999999999")).thenReturn("hash123");
        when(cardRepository.existsByCardHash("hash123")).thenReturn(true);

        assertThrows(CardAlreadyExistsException.class, () -> cardService.createCard(cardRequest));
        verify(cardRepository).existsByCardHash("hash123");
        verify(cardRepository, never()).save(any(Card.class));
    }

    @Test
    @DisplayName("Should lookup card successfully")
    void shouldLookupCardSuccessfully() {
        when(encryptionService.hash("4456897999999999")).thenReturn("hash123");
        when(cardRepository.findByCardHash("hash123")).thenReturn(Optional.of(card));
        when(cardMapper.toLookupResponse(any(Card.class), eq("4456897999999999"))).thenReturn(lookupResponse);


        CardLookupResponse result = cardService.lookupCard("4456897999999999");

        assertNotNull(result);
        verify(cardRepository).findByCardHash("hash123");
        verify(cardMapper).toLookupResponse(any(Card.class), eq("4456897999999999"));
    }

    @Test
    @DisplayName("Should throw exception when card not found")
    void shouldThrowExceptionWhenCardNotFound() {
        when(encryptionService.hash("4456897999999999")).thenReturn("hash123");
        when(cardRepository.findByCardHash("hash123")).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> cardService.lookupCard("4456897999999999"));
        verify(cardRepository).findByCardHash("hash123");
        verify(cardMapper, never()).toLookupResponse(any(), any());
    }

    @Test
    @DisplayName("Should handle card number with spaces")
    void shouldHandleCardNumberWithSpaces() {
        CardRequest requestWithSpaces = CardRequest.builder()
                .cardNumber("4456 8979 9999 9999")
                .build();

        when(encryptionService.hash("4456897999999999")).thenReturn("hash123");
        when(cardRepository.existsByCardHash("hash123")).thenReturn(false);
        when(encryptionService.encrypt("4456897999999999")).thenReturn("encrypted123");
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toResponse(any(Card.class), eq("4456897999999999"))).thenReturn(cardResponse);

        CardResponse result = cardService.createCard(requestWithSpaces);

        assertNotNull(result);
        verify(encryptionService).hash("4456897999999999");
        verify(encryptionService).encrypt("4456897999999999");
    }
}
