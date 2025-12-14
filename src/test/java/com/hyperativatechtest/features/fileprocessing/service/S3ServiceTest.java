package com.hyperativatechtest.features.fileprocessing.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("S3Service Tests")
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    private MultipartFile file;
    private String fileContent;

    @BeforeEach
    void setUp() {
        fileContent = """
                DESAFIO-HYPERATIVA           20180524LOTE0001000002
                C1     4456897999999999
                C2     4456897922969999
                LOTE0001000002
                """;

        file = new MockMultipartFile(
                "file",
                "cards.txt",
                "text/plain",
                fileContent.getBytes()
        );
    }

    @Test
    @DisplayName("Should upload file successfully")
    void shouldUploadFileSuccessfully() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String result = s3Service.uploadFile(file);

        assertNotNull(result);
        assertTrue(result.contains("cards.txt"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Should download file as lines successfully")
    void shouldDownloadFileAsLinesSuccessfully() {
        ResponseInputStream<GetObjectResponse> responseStream = new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                new ByteArrayInputStream(fileContent.getBytes())
        );

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(responseStream);

        List<String> lines = s3Service.downloadFileAsLines("s3://bucket/file.txt");

        assertNotNull(lines);
        assertEquals(4, lines.size());
        assertTrue(lines.get(0).contains("DESAFIO-HYPERATIVA"));
        assertTrue(lines.get(1).contains("C1"));
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    @DisplayName("Should handle upload failure")
    void shouldHandleUploadFailure() throws IOException {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(new RuntimeException("S3 connection failed"));

        assertThrows(RuntimeException.class, () -> s3Service.uploadFile(file));
    }

    @Test
    @DisplayName("Should handle download failure")
    void shouldHandleDownloadFailure() {
        when(s3Client.getObject(any(GetObjectRequest.class)))
                .thenThrow(new RuntimeException("File not found"));

        assertThrows(RuntimeException.class, () -> s3Service.downloadFileAsLines("s3://bucket/missing.txt"));
    }

    @Test
    @DisplayName("Should handle empty file upload")
    void shouldHandleEmptyFileUpload() throws IOException {
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String result = s3Service.uploadFile(emptyFile);

        assertNotNull(result);
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }
}
