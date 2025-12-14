package com.hyperativatechtest.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestAwsConfig {

    @Bean
    @Primary
    public S3Client s3Client() {
        return mock(S3Client.class);
    }

    @Bean
    @Primary
    public S3Presigner s3Presigner() {
        return mock(S3Presigner.class);
    }

    @Bean
    @Primary
    public DynamoDbClient dynamoDbClient() {
        return mock(DynamoDbClient.class);
    }

    @Bean
    @Primary
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return mock(DynamoDbEnhancedClient.class);
    }
}

