package com.hyperativatechtest.features.card.listener;

import com.hyperativatechtest.features.card.batch.CardBatchService;
import com.hyperativatechtest.features.card.dto.batch.JobStatusResponse;
import com.hyperativatechtest.features.card.model.CardJobStatusEnum;
import com.hyperativatechtest.features.card.parser.CardFileParser;
import com.hyperativatechtest.features.card.parser.dto.ParseResult;
import com.hyperativatechtest.features.card.service.CardFileProcessingJobService;
import com.hyperativatechtest.features.common.config.rabbitmq.Queues;
import com.hyperativatechtest.features.fileprocessing.service.JobStatus;
import com.hyperativatechtest.features.fileprocessing.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CardFileProcessingListener {

    private final S3Service s3Service;
    private final CardFileParser cardFileParser;
    private final CardFileProcessingJobService cardFileProcessingJobService;
    private final CardBatchService cardBatchService;

    @RabbitListener(queues = Queues.FILE_PROCESSING_QUEUE)
    public void processCardFile(Map<String, Object> message) {
        String jobId = (String) message.get("jobId");

        try {
            processJob(jobId);
        } catch (Exception e) {
            handleProcessingError(jobId, e);
        }
    }

    private void processJob(String jobId) {
        Optional<JobStatus> jobStatus = cardFileProcessingJobService.getJobStatus(jobId);

        if (jobStatus.isEmpty()) {
            log.error("Job not found: {}", jobId);
            return;
        }

        processJobWithStatus(jobId, jobStatus.get());
    }

    private void processJobWithStatus(String jobId, JobStatus jobStatus) {
        String s3Key = jobStatus.getS3Key();
        String username = jobStatus.getId();

        cardFileProcessingJobService.updateJobStatus(jobId, createJobStatus(CardJobStatusEnum.PROCESSING.name()));

        List<String> lines = s3Service.downloadFileAsLines(s3Key);
        ParseResult parseResult = cardFileParser.parseFile(lines);

        if (!parseResult.isValid()) {
            String errorMessage = "File parsing failed: " + String.join(", ", parseResult.getErrors());
            cardFileProcessingJobService.markFailed(jobId, errorMessage);
            return;
        }

        String lotId = parseResult.getHeader().getLotId();
        cardBatchService.processBatch(parseResult.getCards(), lotId, username);

        cardFileProcessingJobService.markCompleted(jobId);
    }

    private void handleProcessingError(String jobId, Exception e) {
        log.error("Error processing card file for job: {}", jobId, e);
        cardFileProcessingJobService.markFailed(jobId, "Error processing file: " + e.getMessage());
    }

    private JobStatus createJobStatus(String status) {
        return JobStatusResponse.builder()
                .status(status)
                .build();
    }
}

