package com.hyperativatechtest.features.card.mapper;

import com.hyperativatechtest.features.card.model.CardFileProcessingJob;
import com.hyperativatechtest.features.fileprocessing.service.JobStatus;

public class CardJobStatus implements JobStatus {

    private final CardFileProcessingJob job;

    public CardJobStatus(CardFileProcessingJob job) {
        this.job = job;
    }

    @Override
    public String getId() {
        return job.getId();
    }

    @Override
    public String getStatus() {
        return job.getStatus();
    }

    @Override
    public String getFileName() {
        return job.getFileName();
    }

    @Override
    public Integer getTotalRecords() {
        return job.getTotalRecords();
    }

    @Override
    public Integer getProcessedRecords() {
        return job.getProcessedRecords();
    }

    @Override
    public Integer getFailedRecords() {
        return job.getFailedRecords();
    }

    @Override
    public String getErrorMessage() {
        return job.getErrorMessage();
    }

    public String getS3Key() {
        return job.getS3Key();
    }
}

