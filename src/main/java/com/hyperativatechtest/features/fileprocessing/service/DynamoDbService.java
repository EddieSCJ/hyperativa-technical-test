package com.hyperativatechtest.features.fileprocessing.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public abstract class DynamoDbService {

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    protected <T> DynamoDbTable<T> getTable(Class<T> entityClass, String tableName) {
        return dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(entityClass));
    }

    protected String generateJobId() {
        return UUID.randomUUID().toString();
    }

    protected String getCurrentTimestamp() {
        return OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}

