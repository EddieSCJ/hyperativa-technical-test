package com.hyperativatechtest.features.card.service;

import com.hyperativatechtest.features.card.constant.CardBatchMessages;
import com.hyperativatechtest.features.card.dto.batch.FileUploadResponse;
import com.hyperativatechtest.features.card.exception.CardFileUploadException;
import com.hyperativatechtest.features.card.model.CardProcessingJobStatus;
import com.hyperativatechtest.features.fileprocessing.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardBatchUploadService {

    private final S3Service s3Service;
    private final CardFileProcessingJobService cardFileProcessingJobService;

    public FileUploadResponse uploadCardsBatch(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String username = getCurrentUsername();

        try {
            String s3Key = s3Service.uploadFile(file);
            log.debug("File uploaded to S3: {}", s3Key);

            String jobId = cardFileProcessingJobService.createJob(fileName, s3Key, username);

            return FileUploadResponse.builder()
                    .jobId(jobId)
                    .status(CardProcessingJobStatus.PENDING.getValue())
                    .message(CardBatchMessages.FILE_UPLOADED_SUCCESSFULLY)
                    .build();
        } catch (Exception e) {
            log.error("Error uploading file: {}", fileName, e);
            throw new CardFileUploadException(CardBatchMessages.ERROR_UPLOADING_FILE + ": " + e.getMessage(), e);
        }
    }

    private String getCurrentUsername() {
        try {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } catch (Exception e) {
            return "system";
        }
    }
}

