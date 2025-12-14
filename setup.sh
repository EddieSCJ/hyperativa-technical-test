#!/bin/bash

# Hyperativa Tech Test - Main Setup Script
# Orchestrates the setup and running of the entire project

set -e

echo "=========================================="
echo "Hyperativa Tech Test - Setup & Run"
echo "=========================================="
echo ""

# Check prerequisites
echo "Checking prerequisites..."
command -v java >/dev/null 2>&1 || { echo "Java 21 is required but not installed."; exit 1; }
command -v docker >/dev/null 2>&1 || { echo "Docker is required but not installed."; exit 1; }
command -v git >/dev/null 2>&1 || { echo "Git is required but not installed."; exit 1; }

echo "✓ All prerequisites found"
echo ""

# Run modular scripts
bash ./scripts/start-services.sh || exit 1
bash ./scripts/build-project.sh || exit 1
bash ./scripts/run-app.sh

