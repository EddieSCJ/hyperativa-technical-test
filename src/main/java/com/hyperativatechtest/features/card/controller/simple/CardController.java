package com.hyperativatechtest.features.card.controller.simple;

import com.hyperativatechtest.features.card.dto.simple.CardLookupResponse;
import com.hyperativatechtest.features.card.dto.simple.CardRequest;
import com.hyperativatechtest.features.card.dto.simple.CardResponse;
import com.hyperativatechtest.features.card.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CardController implements CardControllerApi {

    private final CardService cardService;

    @Override
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request) {
        log.debug("Creating individual card");
        CardResponse response = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<CardLookupResponse> lookupCard(String cardNumber) {
        log.debug("Lookup card request for card: {}", cardNumber);
        CardLookupResponse response = cardService.lookupCard(cardNumber);
        return ResponseEntity.ok(response);
    }
}

