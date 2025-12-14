package com.hyperativatechtest.features.card.mapper;

import com.hyperativatechtest.features.card.dto.batch.JobStatusResponse;
import com.hyperativatechtest.features.fileprocessing.service.FileProcessingJobService;
import org.springframework.stereotype.Component;

@Component
public class JobStatusMapper {

    public JobStatusResponse toJobStatusResponse(FileProcessingJobService.JobStatus jobStatus) {
        return JobStatusResponse.builder()
                .jobId(jobStatus.getId())
                .status(jobStatus.getStatus())
                .fileName(jobStatus.getFileName())
                .totalRecords(jobStatus.getTotalRecords())
                .processedRecords(jobStatus.getProcessedRecords())
                .failedRecords(jobStatus.getFailedRecords())
                .errorMessage(jobStatus.getErrorMessage())
                .build();
    }
}

