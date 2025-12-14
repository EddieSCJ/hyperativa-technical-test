package com.hyperativatechtest.features.card.listener;

import com.hyperativatechtest.features.card.batch.CardBatchService;
import com.hyperativatechtest.features.card.parser.CardFileParser;
import com.hyperativatechtest.features.card.parser.dto.ParseResult;
import com.hyperativatechtest.features.card.parser.dto.ParsedCard;
import com.hyperativatechtest.features.card.parser.dto.ParsedHeader;
import com.hyperativatechtest.features.card.service.CardFileProcessingJobService;
import com.hyperativatechtest.features.fileprocessing.service.JobStatus;
import com.hyperativatechtest.features.fileprocessing.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardFileProcessingListener Tests")
class CardFileProcessingListenerTest {

    @Mock(lenient = true)
    private S3Service s3Service;

    @Mock(lenient = true)
    private CardFileParser cardFileParser;

    @Mock(lenient = true)
    private CardFileProcessingJobService cardFileProcessingJobService;

    @Mock(lenient = true)
    private CardBatchService cardBatchService;

    @Mock(lenient = true)
    private JobStatus jobStatus;

    @InjectMocks
    private CardFileProcessingListener listener;

    private Map<String, Object> message;
    private List<String> fileLines;
    private ParseResult parseResult;

    @BeforeEach
    void setUp() {
        message = Map.of("jobId", "job123");

        fileLines = Arrays.asList(
                "DESAFIO-HYPERATIVA           20180524LOTE0001000002",
                "C1     4456897999999999",
                "C2     4456897922969999",
                "LOTE0001000002"
        );

        ParsedHeader header = ParsedHeader.builder()
                .name("DESAFIO-HYPERATIVA")
                .date("20180524")
                .lotId("LOTE0001")
                .recordCount(2)
                .build();

        List<ParsedCard> cards = Arrays.asList(
                ParsedCard.builder().cardNumber("4456897999999999").sequenceNumber(1).build(),
                ParsedCard.builder().cardNumber("4456897922969999").sequenceNumber(2).build()
        );

        parseResult = ParseResult.builder()
                .header(header)
                .cards(cards)
                .errors(Collections.emptyList())
                .valid(true)
                .build();

        when(jobStatus.getS3Key()).thenReturn("s3://bucket/file.txt");
        when(jobStatus.getId()).thenReturn("user123");
    }

    @Test
    @DisplayName("Should process card file successfully")
    void shouldProcessCardFileSuccessfully() {
        when(cardFileProcessingJobService.getJobStatus("job123")).thenReturn(Optional.of(jobStatus));
        when(s3Service.downloadFileAsLines("s3://bucket/file.txt")).thenReturn(fileLines);
        when(cardFileParser.parseFile(fileLines)).thenReturn(parseResult);

        listener.processCardFile(message);

        verify(cardFileProcessingJobService).updateJobStatus(eq("job123"), any());
        verify(s3Service).downloadFileAsLines("s3://bucket/file.txt");
        verify(cardFileParser).parseFile(fileLines);
        verify(cardBatchService).processBatch(parseResult.getCards(), "LOTE0001", "user123");
        verify(cardFileProcessingJobService).markCompleted("job123");
    }

    @Test
    @DisplayName("Should handle job not found")
    void shouldHandleJobNotFound() {
        when(cardFileProcessingJobService.getJobStatus("job123")).thenReturn(Optional.empty());

        listener.processCardFile(message);

        verify(cardFileProcessingJobService).getJobStatus("job123");
        verify(s3Service, never()).downloadFileAsLines(any());
        verify(cardFileProcessingJobService, never()).markCompleted(any());
    }

    @Test
    @DisplayName("Should handle parsing errors")
    void shouldHandleParsingErrors() {
        ParseResult invalidResult = ParseResult.builder()
                .valid(false)
                .errors(Arrays.asList("Invalid header", "Invalid footer"))
                .build();

        when(cardFileProcessingJobService.getJobStatus("job123")).thenReturn(Optional.of(jobStatus));
        when(s3Service.downloadFileAsLines("s3://bucket/file.txt")).thenReturn(fileLines);
        when(cardFileParser.parseFile(fileLines)).thenReturn(invalidResult);

        listener.processCardFile(message);

        verify(cardFileProcessingJobService).markFailed(eq("job123"), contains("File parsing failed"));
        verify(cardBatchService, never()).processBatch(any(), any(), any());
        verify(cardFileProcessingJobService, never()).markCompleted(any());
    }

    @Test
    @DisplayName("Should handle processing exceptions")
    void shouldHandleProcessingExceptions() {
        when(cardFileProcessingJobService.getJobStatus("job123")).thenReturn(Optional.of(jobStatus));
        when(s3Service.downloadFileAsLines("s3://bucket/file.txt")).thenThrow(new RuntimeException("S3 error"));

        listener.processCardFile(message);

        verify(cardFileProcessingJobService).markFailed(eq("job123"), contains("Error processing file"));
        verify(cardBatchService, never()).processBatch(any(), any(), any());
        verify(cardFileProcessingJobService, never()).markCompleted(any());
    }
}
