package com.hyperativatechtest.features.card.dto.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String jobId;
    private String status;
    private String presignedUrl;
    private String message;
}

