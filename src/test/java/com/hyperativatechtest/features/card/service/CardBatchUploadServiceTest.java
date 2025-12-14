package com.hyperativatechtest.features.card.service;

import com.hyperativatechtest.features.card.dto.batch.FileUploadResponse;
import com.hyperativatechtest.features.card.exception.CardFileUploadException;
import com.hyperativatechtest.features.common.service.MessagePublisherService;
import com.hyperativatechtest.features.fileprocessing.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CardBatchUploadService Tests")
class CardBatchUploadServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private CardFileProcessingJobService cardFileProcessingJobService;

    @Mock
    private MessagePublisherService messagePublisherService;

    @Mock(lenient = true)
    private SecurityContext securityContext;

    @Mock(lenient = true)
    private Authentication authentication;

    @InjectMocks
    private CardBatchUploadService uploadService;

    private MockMultipartFile file;

    @BeforeEach
    void setUp() {
        file = new MockMultipartFile(
                "file",
                "cards.txt",
                "text/plain",
                "test content".getBytes()
        );

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("testuser");
    }

    @Test
    @DisplayName("Should upload file successfully")
    void shouldUploadFileSuccessfully() throws IOException {
        when(s3Service.uploadFile(file)).thenReturn("s3://bucket/file.txt");
        when(cardFileProcessingJobService.createJob("cards.txt", "s3://bucket/file.txt", "testuser"))
                .thenReturn("job123");

        FileUploadResponse response = uploadService.uploadCardsBatch(file);

        assertNotNull(response);
        assertEquals("job123", response.getJobId());
        assertEquals("PENDING", response.getStatus());

        verify(s3Service).uploadFile(file);
        verify(cardFileProcessingJobService).createJob("cards.txt", "s3://bucket/file.txt", "testuser");
        verify(messagePublisherService).publish(anyString(), any(Map.class), any(Optional.class));
    }

    @Test
    @DisplayName("Should handle S3 upload failure")
    void shouldHandleS3UploadFailure() throws IOException {
        when(s3Service.uploadFile(file)).thenThrow(new RuntimeException("S3 error"));

        assertThrows(CardFileUploadException.class, () -> uploadService.uploadCardsBatch(file));

        verify(s3Service).uploadFile(file);
        verify(cardFileProcessingJobService, never()).createJob(any(), any(), any());
        verify(messagePublisherService, never()).publish(any(), any(), any());
    }

    @Test
    @DisplayName("Should handle job creation failure")
    void shouldHandleJobCreationFailure() throws IOException {
        when(s3Service.uploadFile(file)).thenReturn("s3://bucket/file.txt");
        when(cardFileProcessingJobService.createJob(any(), any(), any()))
                .thenThrow(new RuntimeException("Job creation failed"));

        assertThrows(CardFileUploadException.class, () -> uploadService.uploadCardsBatch(file));

        verify(s3Service).uploadFile(file);
        verify(cardFileProcessingJobService).createJob("cards.txt", "s3://bucket/file.txt", "testuser");
        verify(messagePublisherService, never()).publish(any(), any(), any());
    }

    @Test
    @DisplayName("Should handle authentication failure gracefully")
    void shouldHandleAuthenticationFailureGracefully() throws IOException {
        when(securityContext.getAuthentication()).thenThrow(new RuntimeException("Auth error"));
        when(s3Service.uploadFile(file)).thenReturn("s3://bucket/file.txt");
        when(cardFileProcessingJobService.createJob("cards.txt", "s3://bucket/file.txt", "system"))
                .thenReturn("job123");

        FileUploadResponse response = uploadService.uploadCardsBatch(file);

        assertNotNull(response);
        assertEquals("job123", response.getJobId());
        verify(cardFileProcessingJobService).createJob("cards.txt", "s3://bucket/file.txt", "system");
    }
}
