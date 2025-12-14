package com.hyperativatechtest.features.card.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the header information of a card batch file.
 * <p>
 * The header is the first line of the file and contains batch metadata.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParsedHeader {
    /**
     * Batch name/identifier extracted from positions [01-29]
     */
    private String name;

    /**
     * Batch date in YYYYMMDD format from positions [30-37]
     */
    private String date;

    /**
     * Lot ID that identifies this batch, extracted from positions [38-45]
     */
    private String lotId;

    /**
     * Expected number of card records in the batch (excluding header/footer)
     * from positions [46-51]
     */
    private int recordCount;
}

