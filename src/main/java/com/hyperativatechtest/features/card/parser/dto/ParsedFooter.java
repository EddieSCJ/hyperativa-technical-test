package com.hyperativatechtest.features.card.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the footer information of a card batch file.
 * <p>
 * The footer is the last line of the file and contains summary information
 * that should match the header.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedFooter {
    /**
     * Lot ID from the footer (positions [01-08])
     * Should match the header lot ID for consistency validation
     */
    private String lotId;

    /**
     * Record count from the footer (positions [09-14])
     * Should match the header record count
     */
    private int recordCount;
}

