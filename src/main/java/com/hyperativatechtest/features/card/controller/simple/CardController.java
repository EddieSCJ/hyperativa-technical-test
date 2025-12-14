package com.hyperativatechtest.features.card.controller.simple;

import com.hyperativatechtest.features.card.dto.*;
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
    public ResponseEntity<CardLookupResponse> lookupCard(@RequestBody CardLookupRequest request) {
        log.debug("Lookup card request");
        CardLookupResponse response = cardService.lookupCard(request.getCardNumber());
        return ResponseEntity.ok(response);
    }
}

