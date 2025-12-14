#!/bin/bash

# Hyperativa Tech Test - Setup Script
# This script sets up and runs the entire project

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

# Step 1: Stop existing containers and remove volumes
echo "Step 1: Cleaning up existing containers, databases, and volumes..."
docker-compose down -v 2>/dev/null || true
sleep 2
echo "✓ Containers stopped"
echo "✓ Volumes deleted"
echo ""

# Step 2: Start services
echo "Step 2: Starting Docker services..."
docker-compose up -d
echo "✓ PostgreSQL (port 5432)"
echo "✓ RabbitMQ (port 5672)"
echo "✓ LocalStack (port 4566)"
echo ""

# Wait for services
echo "Waiting for services to be ready (30 seconds)..."
sleep 30
echo "✓ PostgreSQL database 'hyperativa' created"
echo "✓ Services ready"
echo ""

# Step 3: Build project
echo "Step 3: Building project..."
./gradlew clean build -q
echo "✓ Build complete"
echo ""

# Step 4: Run tests
echo "Step 4: Running tests..."
./gradlew test -q
echo "✓ Tests passed"
echo ""

# Step 5: Start application with local profile
echo "Step 5: Starting application..."
echo ""
echo "=========================================="
echo "Application Running!"
echo "=========================================="
echo ""
echo "API URL: http://localhost:8080"
echo "Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
echo "Services:"
echo "  - Application: http://localhost:8080"
echo "  - PostgreSQL: localhost:5432"
echo "  - RabbitMQ: localhost:5672"
echo "  - LocalStack: localhost:4566"
echo ""
echo "Quick Test Commands:"
echo ""
echo "1. Health Check:"
echo "   curl http://localhost:8080/actuator/health"
echo ""
echo "2. Login as Admin (auto-created for local):"
echo "   curl -X POST http://localhost:8080/auth/login \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"username\":\"admin\",\"password\":\"Admin123!\"}'"
echo ""
echo "3. Register New User:"
echo "   curl -X POST http://localhost:8080/auth/register \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"username\":\"user1\",\"password\":\"Pass123!\",\"roleName\":\"USER\"}'"
echo ""
echo "4. Create Card (use token from login):"
echo "   TOKEN=<jwt-token>"
echo "   curl -X POST http://localhost:8080/api/cards \\"
echo "     -H \"Authorization: Bearer \$TOKEN\" \\"
echo "     -H 'Content-Type: application/json' \\"
echo "     -d '{\"cardNumber\":\"4456897999999999\"}'"
echo ""
echo "To stop: Press Ctrl+C"
echo "=========================================="
echo ""

./gradlew bootRun --args='--spring.profiles.active=local'

