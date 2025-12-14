package com.hyperativatechtest.features.card.controller.batch;

import com.hyperativatechtest.features.card.constant.CardBatchMessages;
import com.hyperativatechtest.features.card.dto.batch.FileUploadResponse;
import com.hyperativatechtest.features.card.dto.batch.JobStatusResponse;
import com.hyperativatechtest.features.card.mapper.JobStatusMapper;
import com.hyperativatechtest.features.card.service.CardBatchUploadService;
import com.hyperativatechtest.features.card.service.CardFileProcessingJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static java.util.Objects.nonNull;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BatchCardController implements BatchCardControllerApi {

    private final CardBatchUploadService cardBatchUploadService;
    private final CardFileProcessingJobService cardFileProcessingJobService;
    private final JobStatusMapper jobStatusMapper;

    @Override
    public ResponseEntity<FileUploadResponse> uploadCardsBatch(
            @RequestParam("file") MultipartFile file) {

        log.debug("Received batch cards file upload: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(FileUploadResponse.builder()
                            .message(CardBatchMessages.FILE_IS_EMPTY)
                            .build());
        }

        ResponseEntity<FileUploadResponse> validationError = validateFileFormat(file);
        if (nonNull(validationError)) {
            return validationError;
        }

        FileUploadResponse response = cardBatchUploadService.uploadCardsBatch(file);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Override
    public ResponseEntity<JobStatusResponse> getBatchCardInsertionStatus(@PathVariable String jobId) {
        log.debug("Get batch insertion status for job: {}", jobId);

        return cardFileProcessingJobService.getJobStatus(jobId)
                .map(jobStatusMapper::toJobStatusResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<FileUploadResponse> validateFileFormat(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".txt")) {
            return ResponseEntity.badRequest()
                    .body(FileUploadResponse.builder()
                            .message(CardBatchMessages.ONLY_TXT_FILES_ALLOWED)
                            .build());
        }
        return null;
    }
}