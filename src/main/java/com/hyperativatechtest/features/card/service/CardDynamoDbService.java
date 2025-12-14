package com.hyperativatechtest.features.card.service;

import com.hyperativatechtest.features.card.model.CardFileProcessingJob;
import com.hyperativatechtest.features.card.model.CardJobStatusEnum;
import com.hyperativatechtest.features.fileprocessing.service.DynamoDbService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class CardDynamoDbService extends DynamoDbService {

    private static final String CARD_JOBS_TABLE = "card-file-processing-jobs";
    private DynamoDbTable<CardFileProcessingJob> cardJobsTable;

    public CardDynamoDbService(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        super(dynamoDbEnhancedClient);
    }

    @PostConstruct
    public void init() {
        this.cardJobsTable = getTable(CardFileProcessingJob.class, CARD_JOBS_TABLE);
        log.info("Card DynamoDB table initialized: {}", CARD_JOBS_TABLE);
    }

    public CardFileProcessingJob saveCardProcessingJob(CardFileProcessingJob job) {
        if (isNull(job.getId())) {
            job.setId(generateJobId());
        }
        if (isNull(job.getCreatedAt())) {
            job.setCreatedAt(getCurrentTimestamp());
        }

        job.setUpdatedAt(getCurrentTimestamp());

        cardJobsTable.putItem(job);
        log.debug("Saved card processing job with id: {}", job.getId());
        return job;
    }

    public Optional<CardFileProcessingJob> getCardProcessingJob(String jobId) {
        CardFileProcessingJob job = cardJobsTable.getItem(r -> r.key(k -> k.partitionValue(jobId)));
        return Optional.ofNullable(job);
    }

    public void updateCardProcessingJob(CardFileProcessingJob job) {
        job.setUpdatedAt(getCurrentTimestamp());
        cardJobsTable.updateItem(job);
        log.debug("Updated card processing job: {}", job.getId());
    }

    public void completeCardJob(String jobId) {
        getCardProcessingJob(jobId).ifPresent(job -> {
            job.setStatus(CardJobStatusEnum.COMPLETED.name());
            updateCardProcessingJob(job);
            log.debug("Card job {} marked as completed", jobId);
        });
    }

    public void failCardJob(String jobId, String errorMessage) {
        getCardProcessingJob(jobId).ifPresent(job -> {
            job.setStatus(CardJobStatusEnum.FAILED.name());
            job.setErrorMessage(errorMessage);
            updateCardProcessingJob(job);
            log.error("Card job {} marked as failed: {}", jobId, errorMessage);
        });
    }
}

