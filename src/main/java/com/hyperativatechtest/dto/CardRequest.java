package com.hyperativatechtest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CardRequest(
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be between 13 and 19 digits")
    String cardNumber,
    String lotId,
    Integer sequenceNumber
) {}

