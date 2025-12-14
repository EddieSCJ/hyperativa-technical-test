package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.entity.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardBatchInsertService Tests")
class CardBatchInsertServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private CardBatchInsertService cardBatchInsertService;

    private List<Card> cards;

    @BeforeEach
    void setUp() {
        Card card1 = Card.builder()
                .id(UUID.randomUUID())
                .encryptedCardNumber("encrypted1")
                .cardHash("hash1")
                .lotId("LOT001")
                .sequenceNumber(1)
                .createdBy("user1")
                .build();

        Card card2 = Card.builder()
                .id(UUID.randomUUID())
                .encryptedCardNumber("encrypted2")
                .cardHash("hash2")
                .lotId("LOT001")
                .sequenceNumber(2)
                .createdBy("user1")
                .build();

        cards = Arrays.asList(card1, card2);
    }

    @Test
    @DisplayName("Should build correct batch insert SQL")
    void shouldBuildCorrectBatchInsertSql() {
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(2);

        int result = cardBatchInsertService.batchInsertIgnoreDuplicates(cards);

        assertEquals(2, result);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object[]> paramsCaptor = ArgumentCaptor.forClass(Object[].class);

        verify(jdbcTemplate).update(sqlCaptor.capture(), paramsCaptor.capture());

        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("INSERT INTO cards"));
        assertTrue(sql.contains("VALUES (?, ?, ?, ?, ?, NOW()), (?, ?, ?, ?, ?, NOW())"));
        assertTrue(sql.contains("ON CONFLICT (card_hash) DO NOTHING"));

        Object[] params = paramsCaptor.getValue();
        assertEquals(10, params.length);
        assertEquals("encrypted1", params[0]);
        assertEquals("hash1", params[1]);
        assertEquals("LOT001", params[2]);
        assertEquals(1, params[3]);
        assertEquals("user1", params[4]);
        assertEquals("encrypted2", params[5]);
        assertEquals("hash2", params[6]);
    }

    @Test
    @DisplayName("Should handle empty card list")
    void shouldHandleEmptyCardList() {
        int result = cardBatchInsertService.batchInsertIgnoreDuplicates(Collections.emptyList());

        assertEquals(0, result);
        verify(jdbcTemplate, never()).update(anyString(), any(Object[].class));
    }

    @Test
    @DisplayName("Should handle single card")
    void shouldHandleSingleCard() {
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        int result = cardBatchInsertService.batchInsertIgnoreDuplicates(cards.subList(0, 1));

        assertEquals(1, result);

        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).update(sqlCaptor.capture(), any(Object[].class));

        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("VALUES (?, ?, ?, ?, ?, NOW())"));
        assertFalse(sql.contains(", ("));
    }
}
