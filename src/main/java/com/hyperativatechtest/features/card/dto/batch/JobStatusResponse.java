package com.hyperativatechtest.features.card.dto.batch;

import com.hyperativatechtest.features.fileprocessing.service.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobStatusResponse implements JobStatus {
    private String jobId;
    private String status;
    private String fileName;
    private String lotId;
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer failedRecords;
    private String presignedUrl;
    private String errorMessage;
    private String createdAt;
    private String updatedAt;

    @Override
    public String getId() {
        return this.getJobId();
    }
}

