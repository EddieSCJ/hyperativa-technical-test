package com.hyperativatechtest.features.card.mapper;

import com.hyperativatechtest.features.card.dto.batch.JobStatusResponse;
import com.hyperativatechtest.features.card.service.CardDownloadService;
import com.hyperativatechtest.features.fileprocessing.service.JobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class JobStatusMapper {

    private final CardDownloadService cardDownloadService;

    public JobStatusResponse toJobStatusResponse(JobStatus jobStatus) {
        String presignedUrl = null;

        if (jobStatus instanceof CardJobStatus cardJobStatus) {
            String s3Key = cardJobStatus.getS3Key();
            if (nonNull(s3Key)) {
                presignedUrl = cardDownloadService.generateDownloadUrl(s3Key);
            }
        }

        return JobStatusResponse.builder()
                .jobId(jobStatus.getId())
                .status(jobStatus.getStatus())
                .fileName(jobStatus.getFileName())
                .totalRecords(jobStatus.getTotalRecords())
                .processedRecords(jobStatus.getProcessedRecords())
                .failedRecords(jobStatus.getFailedRecords())
                .errorMessage(jobStatus.getErrorMessage())
                .presignedUrl(presignedUrl)
                .build();
    }
}

