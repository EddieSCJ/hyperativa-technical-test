package com.hyperativatechtest.features.fileprocessing.service;

import java.util.Optional;

public interface FileProcessingJobService {

    String createJob(String fileName, String s3Key, String username);

    Optional<JobStatus> getJobStatus(String jobId);

    void updateJobStatus(String jobId, JobStatus status);

    void markCompleted(String jobId);

    void markFailed(String jobId, String errorMessage);
}

