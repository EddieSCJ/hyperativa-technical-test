package com.hyperativatechtest.features.card.controller.simple;

import com.hyperativatechtest.features.card.controller.simple.swagger.examples.CreateCardExample;
import com.hyperativatechtest.features.card.controller.simple.swagger.examples.LookupCardExample;
import com.hyperativatechtest.features.card.dto.simple.CardLookupResponse;
import com.hyperativatechtest.features.card.dto.simple.CardRequest;
import com.hyperativatechtest.features.card.dto.simple.CardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/cards")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cards", description = "Individual card management - create and lookup single cards")
public interface CardControllerApi {

    @PostMapping
    @Operation(summary = "Create a new card", description = "Store a new card number securely")
    @RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CardRequest.class),
            examples = @ExampleObject(value = CreateCardExample.REQUEST)
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Card created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CardResponse.class),
                examples = @ExampleObject(value = CreateCardExample.CARD_CREATED_201_OK)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid request - malformed JSON or invalid card data",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Invalid Card Number",
                        value = CreateCardExample.ERROR_INVALID_CARD_NUMBER
                    ),
                    @ExampleObject(
                        name = "Malformed JSON",
                        value = CreateCardExample.ERROR_MALFORMED_JSON
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token required",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = CreateCardExample.ERROR_UNAUTHORIZED)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Card already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = CreateCardExample.ERROR_CARD_ALREADY_EXISTS)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = CreateCardExample.ERROR_INTERNAL_SERVER)
            )
        )
    })
    ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardRequest request);

    @GetMapping("/lookup")
    @Operation(summary = "Lookup a card", description = "Check if a card exists and get its unique identifier")
    @Parameter(
        name = "cardNumber",
        description = "The card number to lookup",
        required = true,
        example = "4456897999999999"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Card found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CardLookupResponse.class),
                examples = @ExampleObject(value = LookupCardExample.CARD_FOUND_200_OK)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid card number format",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = LookupCardExample.ERROR_INVALID_CARD_NUMBER)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token required",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = LookupCardExample.ERROR_UNAUTHORIZED)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Card not found"
        )
    })
    ResponseEntity<CardLookupResponse> lookupCard(@RequestParam String cardNumber);
}
