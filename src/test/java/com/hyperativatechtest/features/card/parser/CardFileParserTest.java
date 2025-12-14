package com.hyperativatechtest.features.card.parser;

import com.hyperativatechtest.features.card.parser.dto.ParseResult;
import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import com.hyperativatechtest.features.card.parser.dto.ParsedFooter;
import com.hyperativatechtest.features.card.parser.dto.ParsedHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardFileParser Tests")
class CardFileParserTest {

    @InjectMocks
    private CardFileParser cardFileParser;

    private List<String> validFileLines;
    private List<String> invalidFileLines;

    @BeforeEach
    void setUp() {
        validFileLines = Arrays.asList(
                "DESAFIO-HYPERATIVA           20180524LOTE0001000003",
                "C2     4456897999999999                   ",
                "C1     4456897922969999                   ",
                "C3     4456897999999999                   ",
                "LOTE0001000003                            "
        );

        invalidFileLines = Arrays.asList(
                "INVALID HEADER                            ",
                "C1     INVALID_CARD                        ",
                "INVALID FOOTER                            "
        );
    }

    @Test
    @DisplayName("Should parse valid file successfully")
    void shouldParseValidFileSuccessfully() {
        ParseResult result = cardFileParser.parseFile(validFileLines);

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());

        ParsedHeader header = result.getHeader();
        assertNotNull(header);
        assertEquals("DESAFIO-HYPERATIVA", header.getName());
        assertEquals("20180524", header.getDate());
        assertEquals("LOTE0001", header.getLotId());
        assertEquals(3, header.getRecordCount());

        List<ParsedCard> cards = result.getCards();
        assertEquals(3, cards.size());

        ParsedCard firstCard = cards.get(0);
        assertEquals("C", firstCard.getLineIdentifier());
        assertEquals(2, firstCard.getSequenceNumber());
        assertEquals("4456897999999999", firstCard.getCardNumber());

        ParsedFooter footer = result.getFooter();
        assertNotNull(footer);
        assertEquals("LOTE0001", footer.getLotId());
        assertEquals(3, footer.getRecordCount());
    }

    @Test
    @DisplayName("Should handle empty file")
    void shouldHandleEmptyFile() {
        ParseResult result = cardFileParser.parseFile(Collections.emptyList());

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("File is empty", result.getErrors().get(0));
    }

    @Test
    @DisplayName("Should handle null file")
    void shouldHandleNullFile() {
        ParseResult result = cardFileParser.parseFile(null);

        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals("File is empty", result.getErrors().get(0));
    }

    @Test
    @DisplayName("Should parse single card line")
    void shouldParseSingleCardLine() {
        ParsedCard result = cardFileParser.parseCardLine("C2     4456897999999999                   ", 2);

        assertNotNull(result);
        assertEquals("C", result.getLineIdentifier());
        assertEquals(2, result.getSequenceNumber());
        assertEquals("4456897999999999", result.getCardNumber());
        assertEquals(2, result.getLineNumber());
    }

    @Test
    @DisplayName("Should return null for non-card line")
    void shouldReturnNullForNonCardLine() {
        ParsedCard result = cardFileParser.parseCardLine("HEADER LINE                              ", 1);

        assertNull(result);
    }

    @Test
    @DisplayName("Should throw exception for short line")
    void shouldThrowExceptionForShortLine() {
        assertThrows(IllegalArgumentException.class,
                () -> cardFileParser.parseCardLine("C1", 1));
    }

    @Test
    @DisplayName("Should handle invalid sequence number")
    void shouldHandleInvalidSequenceNumber() {
        assertThrows(IllegalArgumentException.class,
                () -> cardFileParser.parseCardLine("CABCD  4456897999999999", 1));
    }

    @Test
    @DisplayName("Should validate record count mismatch")
    void shouldValidateRecordCountMismatch() {
        List<String> mismatchLines = Arrays.asList(
                "DESAFIO-HYPERATIVA           20180524LOTE0001000005",
                "C1     4456897999999999",
                "C2     4456897922969999",
                "LOTE0001000003"
        );

        ParseResult result = cardFileParser.parseFile(mismatchLines);

        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(error -> error.contains("Record count mismatch")));
    }

    @Test
    @DisplayName("Should handle file with only header and footer")
    void shouldHandleFileWithOnlyHeaderAndFooter() {
        List<String> headerFooterOnly = Arrays.asList(
                "DESAFIO-HYPERATIVA           20180524LOTE0001000000",
                "LOTE0001000000"
        );

        ParseResult result = cardFileParser.parseFile(headerFooterOnly);

        assertTrue(result.isValid());
        assertEquals(0, result.getCards().size());
        assertEquals(0, result.getHeader().getRecordCount());
        assertEquals(0, result.getFooter().getRecordCount());
    }
}
