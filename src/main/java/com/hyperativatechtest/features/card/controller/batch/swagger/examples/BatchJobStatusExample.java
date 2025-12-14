package com.hyperativatechtest.features.card.controller.batch.swagger.examples;

public class BatchJobStatusExample {

    public static final String RESPONSE_PENDING = """
        {
          "jobId": "550e8400-e29b-41d4-a716-446655440000",
          "status": "PENDING",
          "fileName": "cards.txt",
          "lotId": null,
          "totalRecords": 0,
          "processedRecords": 0,
          "failedRecords": 0,
          "presignedUrl": "https://s3.amazonaws.com/...",
          "errorMessage": null,
          "createdAt": "2024-12-14T02:50:00Z",
          "updatedAt": "2024-12-14T02:50:00Z"
        }
        """;

    public static final String RESPONSE_PROCESSING = """
        {
          "jobId": "550e8400-e29b-41d4-a716-446655440000",
          "status": "PROCESSING",
          "fileName": "cards.txt",
          "lotId": "LOTE0001",
          "totalRecords": 10,
          "processedRecords": 5,
          "failedRecords": 0,
          "presignedUrl": "https://s3.amazonaws.com/...",
          "errorMessage": null,
          "createdAt": "2024-12-14T02:50:00Z",
          "updatedAt": "2024-12-14T02:50:30Z"
        }
        """;

    public static final String RESPONSE_COMPLETED = """
        {
          "jobId": "550e8400-e29b-41d4-a716-446655440000",
          "status": "COMPLETED",
          "fileName": "cards.txt",
          "lotId": "LOTE0001",
          "totalRecords": 10,
          "processedRecords": 10,
          "failedRecords": 0,
          "presignedUrl": "https://s3.amazonaws.com/...",
          "errorMessage": null,
          "createdAt": "2024-12-14T02:50:00Z",
          "updatedAt": "2024-12-14T02:50:45Z"
        }
        """;

    public static final String ERROR_NOT_FOUND = """
        {
          "status": 404,
          "message": "Job not found: 550e8400-e29b-41d4-a716-446655440000"
        }
        """;

    private BatchJobStatusExample() {}
}

