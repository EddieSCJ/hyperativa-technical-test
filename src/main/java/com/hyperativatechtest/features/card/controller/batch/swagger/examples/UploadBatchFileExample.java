package com.hyperativatechtest.features.card.controller.batch.swagger.examples;

public class UploadBatchFileExample {

    public static final String RESPONSE_202 = """
        {
          "jobId": "550e8400-e29b-41d4-a716-446655440000",
          "status": "PENDING",
          "presignedUrl": "https://s3.amazonaws.com/card-files-bucket/uploads/.../cards.txt?...",
          "message": "File uploaded successfully. Processing started."
        }
        """;

    public static final String ERROR_EMPTY = """
        {
          "jobId": null,
          "status": null,
          "presignedUrl": null,
          "message": "File is empty"
        }
        """;

    public static final String ERROR_INVALID_TYPE = """
        {
          "jobId": null,
          "status": null,
          "presignedUrl": null,
          "message": "Only TXT files are allowed"
        }
        """;

    private UploadBatchFileExample() {}
}

