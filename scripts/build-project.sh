#!/bin/bash

# Build and test the project

set -e

echo "Step 3: Building project..."
./gradlew clean build -q
echo "✓ Build complete"
echo ""

echo "Step 4: Running tests..."
./gradlew test -q
echo "✓ Tests passed"
echo ""

exit 0

