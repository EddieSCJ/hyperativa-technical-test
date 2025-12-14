#!/bin/bash

echo "Initializing LocalStack resources..."

# Wait for LocalStack to be ready
sleep 8

# Create S3 bucket for file uploads
echo "Creating S3 bucket..."
awslocal s3 mb s3://card-files-bucket --region us-east-1 2>&1 || {
    echo "ℹ S3 bucket already exists or error creating it"
}
echo "✓ S3 bucket: card-files-bucket"

# Create DynamoDB table for file processing jobs
echo "Creating DynamoDB table..."
awslocal dynamodb create-table \
    --table-name file-processing-jobs \
    --attribute-definitions AttributeName=id,AttributeType=S \
    --key-schema AttributeName=id,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region us-east-1 2>&1 || {
    echo "ℹ DynamoDB table already exists or error creating it"
}

echo "✓ DynamoDB table: file-processing-jobs"

echo ""
echo "============================================"
echo "✓ LocalStack initialization complete!"
echo "============================================"
echo ""