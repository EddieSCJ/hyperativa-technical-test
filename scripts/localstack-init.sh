#!/bin/bash

echo "Initializing LocalStack resources..."

# Wait for LocalStack to be ready
sleep 5

# Create S3 bucket for file uploads
awslocal s3 mb s3://card-files-bucket
echo "Created S3 bucket: card-files-bucket"

# Create DynamoDB table for file processing jobs
awslocal dynamodb create-table \
    --table-name file-processing-jobs \
    --attribute-definitions \
        AttributeName=id,AttributeType=S \
    --key-schema \
        AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

echo "Created DynamoDB table: file-processing-jobs"

# Create GSI for status queries
awslocal dynamodb update-table \
    --table-name file-processing-jobs \
    --attribute-definitions \
        AttributeName=status,AttributeType=S \
        AttributeName=createdAt,AttributeType=S \
    --global-secondary-index-updates \
        "[{\"Create\":{\"IndexName\":\"status-createdAt-index\",\"KeySchema\":[{\"AttributeName\":\"status\",\"KeyType\":\"HASH\"},{\"AttributeName\":\"createdAt\",\"KeyType\":\"RANGE\"}],\"Projection\":{\"ProjectionType\":\"ALL\"}}}]"

echo "Created GSI: status-createdAt-index"

echo "LocalStack initialization complete!"

