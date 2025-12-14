package com.hyperativatechtest.features.card.parser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParseResult {
    private ParsedHeader header;
    private List<ParsedCard> cards;
    private ParsedFooter footer;
    private List<String> errors;

    /**
     * True if parsing completed without errors, false otherwise
     * Use this flag to determine if the result is safe to process
     */
    private boolean valid;
}

