package com.hyperativatechtest.features.card.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a single parsed credit card from the file.
 * <p>
 * Each card line contains the card number and its position within the batch.
 * Card numbers are validated to be between 13-19 digits.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedCard {
    /**
     * Line identifier character, typically 'C' for card line
     * from position [01-01]
     */
    private String lineIdentifier;

    /**
     * Sequence number within the batch from positions [02-07]
     * Used to maintain card order and detect missing cards
     */
    private int sequenceNumber;

    /**
     * The credit card number (full 19 digits, left-padded with zeros)
     * extracted from positions [08-26]
     */
    private String cardNumber;

    /**
     * The line number in the source file where this card was found
     * Used for error reporting and tracking
     */
    private int lineNumber;
}

