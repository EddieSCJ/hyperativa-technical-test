package com.hyperativatechtest.features.card.batch;

import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardBatchService Tests")
class CardBatchServiceTest {

    @Mock
    private JobLauncher jobLauncher;

    @Mock
    private Job cardProcessingJob;

    @Mock
    private CardItemReader cardItemReader;

    @Mock
    private JobExecution jobExecution;

    @InjectMocks
    private CardBatchService cardBatchService;

    private List<ParsedCard> parsedCards;

    @BeforeEach
    void setUp() {
        ParsedCard card1 = ParsedCard.builder()
                .lineIdentifier("C")
                .sequenceNumber(1)
                .cardNumber("4456897999999999")
                .lineNumber(2)
                .build();

        ParsedCard card2 = ParsedCard.builder()
                .lineIdentifier("C")
                .sequenceNumber(2)
                .cardNumber("4456897922969999")
                .lineNumber(3)
                .build();

        parsedCards = Arrays.asList(card1, card2);
    }

    @Test
    @DisplayName("Should process batch successfully")
    void shouldProcessBatchSuccessfully() throws Exception {
        when(jobLauncher.run(eq(cardProcessingJob), any(JobParameters.class))).thenReturn(jobExecution);

        assertDoesNotThrow(() -> cardBatchService.processBatch(parsedCards, "LOT001", "testuser"));

        verify(cardItemReader).setCards(parsedCards);
        verify(jobLauncher).run(eq(cardProcessingJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("Should handle empty card list")
    void shouldHandleEmptyCardList() throws Exception {
        when(jobLauncher.run(eq(cardProcessingJob), any(JobParameters.class))).thenReturn(jobExecution);

        assertDoesNotThrow(() -> cardBatchService.processBatch(Collections.emptyList(), "LOT001", "testuser"));

        verify(cardItemReader).setCards(Collections.emptyList());
        verify(jobLauncher).run(eq(cardProcessingJob), any(JobParameters.class));
    }

    @Test
    @DisplayName("Should throw exception when job launcher fails")
    void shouldThrowExceptionWhenJobLauncherFails() throws Exception {
        when(jobLauncher.run(eq(cardProcessingJob), any(JobParameters.class)))
                .thenThrow(new RuntimeException("Job failed"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> cardBatchService.processBatch(parsedCards, "LOT001", "testuser"));

        assertTrue(exception.getMessage().contains("Batch processing failed"));
        verify(cardItemReader).setCards(parsedCards);
    }

    @Test
    @DisplayName("Should set correct job parameters")
    void shouldSetCorrectJobParameters() throws Exception {
        when(jobLauncher.run(eq(cardProcessingJob), any(JobParameters.class))).thenReturn(jobExecution);

        cardBatchService.processBatch(parsedCards, "LOT001", "testuser");

        verify(jobLauncher).run(eq(cardProcessingJob), argThat(params ->
                "LOT001".equals(params.getString("lotId")) &&
                "testuser".equals(params.getString("username")) &&
                params.getLong("timestamp") != null
        ));
    }
}
