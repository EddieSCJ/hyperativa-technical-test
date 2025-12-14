package com.hyperativatechtest.features.card.parser;

import com.hyperativatechtest.features.card.parser.dto.ParseResult;
import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import com.hyperativatechtest.features.card.parser.dto.ParsedFooter;
import com.hyperativatechtest.features.card.parser.dto.ParsedHeader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Parser for fixed-width credit card batch files specified in DESAFIO-HYPERATIVA.txt
 * <p>
 * <b>File Structure:</b>
 * <ul>
 *   <li>Line 1: Header (batch metadata)</li>
 *   <li>Lines 2-n-1: Card records</li>
 *   <li>Line n: Footer (summary)</li>
 * </ul>
 * <p>
 * <b>Formats:</b>
 * <ul>
 *   <li>{@code HEADER:  [01-29] Name | [30-37] Date(YYYYMMDD) | [38-45] LotId | [46-51] RecordCount}</li>
 *   <li>{@code CARD:    [01-01] 'C'  | [02-07] Sequence       | [08-26] CardNumber(13-19 digits)}</li>
 *   <li>{@code FOOTER:  [01-08] LotId | [09-14] RecordCount}</li>
 * </ul>
 */

@Component
@Slf4j
public class CardFileParser {

    public ParseResult parseFile(List<String> lines) {
        if (isEmpty(lines)) {
            return emptyFileResult();
        }

        ParsedHeader header = tryParseHeader(lines.getFirst());
        ParsedFooter footer = tryParseFooter(lines.getLast());
        List<ParsedCard> cards = parseAllCards(lines);
        List<String> errors = new ArrayList<>();

        validateHeader(header, errors);
        validateFooter(footer, errors);
        validateRecordCounts(header, footer, cards, errors);

        return buildResult(header, cards, footer, errors);
    }

    public ParsedCard parseCardLine(String line, int lineNumber) {
        if (isTooShort(line)) {
            throw new IllegalArgumentException("Line too short to contain card data");
        }

        String padded = padLine(line);
        return isCardLine(padded) ? extractCard(padded, lineNumber) : null;
    }

    private ParseResult emptyFileResult() {
        return buildResult(null, new ArrayList<>(), null, List.of("File is empty"));
    }

    private boolean isEmpty(List<String> lines) {
        return Objects.isNull(lines) || CollectionUtils.isEmpty(lines);
    }

    private boolean isTooShort(String line) {
        return line == null || line.length() < 26;
    }

    private String padLine(String line) {
        return String.format("%-51s", line);
    }

    private boolean isCardLine(String padded) {
        return padded.substring(0, 1).equalsIgnoreCase("C");
    }

    private ParsedCard extractCard(String line, int lineNumber) {
        return ParsedCard.builder()
                .lineIdentifier(line.substring(0, 1))
                .sequenceNumber(parseSequence(line))
                .cardNumber(line.substring(7, Math.min(26, line.length())).trim())
                .lineNumber(lineNumber)
                .build();
    }

    private int parseSequence(String line) {
        try {
            return Integer.parseInt(line.substring(1, 7).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid sequence number: " + e.getMessage());
        }
    }

    private ParsedHeader tryParseHeader(String line) {
        try {
            return parseHeader(line);
        } catch (Exception e) {
            log.warn("Failed to parse header: {}", e.getMessage());
            return null;
        }
    }

    private ParsedHeader parseHeader(String line) {
        String padded = padToLength(line, 51);
        return ParsedHeader.builder()
                .name(extract(padded, 0, 29))
                .date(extract(padded, 29, 37))
                .lotId(extract(padded, 37, 45))
                .recordCount(parseInt(extract(padded, 45, 51)))
                .build();
    }

    private ParsedFooter tryParseFooter(String line) {
        try {
            return parseFooter(line);
        } catch (Exception e) {
            log.warn("Failed to parse footer: {}", e.getMessage());
            return null;
        }
    }

    private ParsedFooter parseFooter(String line) {
        String padded = padToLength(line, 14);
        return ParsedFooter.builder()
                .lotId(extract(padded, 0, 8))
                .recordCount(parseInt(extract(padded, 8, 14)))
                .build();
    }

    private String padToLength(String line, int length) {
        if (line == null || line.length() < length) {
            return String.format("%-" + length + "s", line != null ? line : "");
        }
        return line;
    }

    private String extract(String line, int start, int end) {
        if (line.length() >= end) {
            return line.substring(start, end).trim();
        }
        return line.length() > start ? line.substring(start).trim() : "";
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private List<ParsedCard> parseAllCards(List<String> lines) {
        List<ParsedCard> cards = new ArrayList<>();
        for (int i = 1; i < lines.size() - 1; i++) {
            parseCardAtLine(lines.get(i), i + 1, cards);
        }
        return cards;
    }

    private void parseCardAtLine(String line, int lineNumber, List<ParsedCard> cards) {
        if (line.trim().isEmpty()) {
            return;
        }

        try {
            ParsedCard card = parseCardLine(line, lineNumber);
            if (card != null && isValidCardNumber(card.getCardNumber())) {
                cards.add(card);
            }
        } catch (Exception e) {
            log.warn("Line {}: {}", lineNumber, e.getMessage());
        }
    }

    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        int digits = cardNumber.replaceAll("\\D", "").length();
        return digits >= 13 && digits <= 19;
    }

    private void validateHeader(ParsedHeader header, List<String> errors) {
        if (header == null) {
            errors.add("Header parsing failed");
        }
    }

    private void validateFooter(ParsedFooter footer, List<String> errors) {
        if (footer == null) {
            errors.add("Footer parsing failed");
        }
    }

    private void validateRecordCounts(ParsedHeader header, ParsedFooter footer,
                                       List<ParsedCard> cards, List<String> errors) {
        if (header == null || footer == null) {
            return;
        }

        if (header.getRecordCount() != footer.getRecordCount()) {
            errors.add("Record count mismatch: header=" + header.getRecordCount() +
                    ", footer=" + footer.getRecordCount());
        }

        if (header.getRecordCount() != cards.size()) {
            log.warn("Declared {} records but parsed {}", header.getRecordCount(), cards.size());
        }
    }

    private ParseResult buildResult(ParsedHeader header, List<ParsedCard> cards,
                                     ParsedFooter footer, List<String> errors) {
        return ParseResult.builder()
                .header(header)
                .cards(cards)
                .footer(footer)
                .errors(errors)
                .valid(errors.isEmpty())
                .build();
    }
}

