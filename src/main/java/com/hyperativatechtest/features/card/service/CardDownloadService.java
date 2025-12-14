package com.hyperativatechtest.features.card.service;

import com.hyperativatechtest.features.fileprocessing.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardDownloadService {

    private final S3Service s3Service;

    public String generateDownloadUrl(String s3Key) {
        try {
            String presignedUrl = s3Service.generatePresignedUrl(s3Key);
            log.debug("Generated presigned URL for s3Key: {}", s3Key);
            return presignedUrl;
        } catch (Exception e) {
            log.error("Error generating presigned URL for s3Key: {}", s3Key, e);
            throw new RuntimeException("Error generating download URL", e);
        }
    }
}

