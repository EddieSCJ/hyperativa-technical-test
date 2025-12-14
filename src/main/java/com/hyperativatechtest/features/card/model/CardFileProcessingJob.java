package com.hyperativatechtest.features.card.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardFileProcessingJob {

    private String id;
    private String status;
    private String s3Key;
    private String fileName;
    private String lotId;
    private String username;
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer failedRecords;
    private String errorMessage;
    private String createdAt;
    private String updatedAt;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "status-createdAt-index")
    @DynamoDbAttribute("status")
    public String getStatus() {
        return status;
    }
}

