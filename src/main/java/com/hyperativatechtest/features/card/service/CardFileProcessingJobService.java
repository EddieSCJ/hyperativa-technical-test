package com.hyperativatechtest.features.card.service;

import com.hyperativatechtest.features.card.mapper.CardJobStatus;
import com.hyperativatechtest.features.card.model.CardFileProcessingJob;
import com.hyperativatechtest.features.card.model.CardJobStatusEnum;
import com.hyperativatechtest.features.fileprocessing.service.FileProcessingJobService;
import com.hyperativatechtest.features.fileprocessing.service.JobStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardFileProcessingJobService implements FileProcessingJobService {

    private final CardDynamoDbService cardDynamoDbService;

    @Override
    public String createJob(String fileName, String s3Key, String username) {
        log.info("Creating card file processing job for file: {}", fileName);

        CardFileProcessingJob job = CardFileProcessingJob.builder()
                .status(CardJobStatusEnum.PENDING.name())
                .s3Key(s3Key)
                .fileName(fileName)
                .username(username)
                .processedRecords(0)
                .failedRecords(0)
                .build();

        CardFileProcessingJob created = cardDynamoDbService.saveCardProcessingJob(job);
        log.info("Card processing job created with id: {}", created.getId());
        return created.getId();
    }

    @Override
    public Optional<JobStatus> getJobStatus(String jobId) {
        return cardDynamoDbService.getCardProcessingJob(jobId)
                .map(CardJobStatus::new);
    }

    @Override
    public void updateJobStatus(String jobId, JobStatus status) {
        cardDynamoDbService.getCardProcessingJob(jobId).ifPresent(job -> {
            job.setStatus(status.getStatus());
            cardDynamoDbService.updateCardProcessingJob(job);
        });
    }

    @Override
    public void markCompleted(String jobId) {
        log.info("Marking card processing job as completed: {}", jobId);
        cardDynamoDbService.completeCardJob(jobId);
    }

    @Override
    public void markFailed(String jobId, String errorMessage) {
        log.error("Marking card processing job as failed: {} - Error: {}", jobId, errorMessage);
        cardDynamoDbService.failCardJob(jobId, errorMessage);
    }
}

