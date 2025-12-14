package com.hyperativatechtest.features.card.controller.batch;

import com.hyperativatechtest.features.card.controller.batch.swagger.examples.BatchJobStatusExample;
import com.hyperativatechtest.features.card.controller.batch.swagger.examples.UploadBatchFileExample;
import com.hyperativatechtest.features.card.dto.batch.FileUploadResponse;
import com.hyperativatechtest.features.card.dto.batch.JobStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequestMapping("/api/batch-cards")
@Tag(name = "Batch Cards", description = "Batch card insertion - upload and process multiple cards from files")
public interface BatchCardControllerApi {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload cards batch file",
        description = "Upload a TXT file containing multiple card numbers for batch processing. " +
                "File is stored in S3 and processed asynchronously via message queue."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "202",
            description = "File accepted for batch processing",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = FileUploadResponse.class),
                examples = @ExampleObject(value = UploadBatchFileExample.RESPONSE_202)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid file",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "Invalid File Type",
                        value = UploadBatchFileExample.ERROR_INVALID_TYPE
                    ),
                    @ExampleObject(
                        name = "Empty File",
                        value = UploadBatchFileExample.ERROR_EMPTY
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json")
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error",
            content = @Content(mediaType = "application/json")
        )
    })
    ResponseEntity<FileUploadResponse> uploadCardsBatch(
        @RequestParam("file") MultipartFile file
    ) throws IOException;

    @GetMapping("/{jobId}/insertion-status")
    @Operation(
        summary = "Get batch insertion status",
        description = "Get the processing status of a batch card insertion job"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Job status retrieved",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = JobStatusResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Pending Status",
                        value = BatchJobStatusExample.RESPONSE_PENDING
                    ),
                    @ExampleObject(
                        name = "Processing Status",
                        value = BatchJobStatusExample.RESPONSE_PROCESSING
                    ),
                    @ExampleObject(
                        name = "Completed Status",
                        value = BatchJobStatusExample.RESPONSE_COMPLETED
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Job not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = BatchJobStatusExample.ERROR_NOT_FOUND)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - JWT token required",
            content = @Content(mediaType = "application/json")
        )
    })
    ResponseEntity<JobStatusResponse> getBatchCardInsertionStatus(@PathVariable String jobId);
}

