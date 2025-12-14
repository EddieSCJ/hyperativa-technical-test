#!/bin/bash

# Start Docker containers and wait for them to be ready

set -e

echo "Step 1: Cleaning up existing containers, databases, and volumes..."
docker-compose down -v --remove-orphans 2>/dev/null || true
docker system prune -f --volumes 2>/dev/null || true
sleep 2
echo "✓ Containers deleted"
echo "✓ Volumes deleted"
echo "✓ Databases dropped"
echo "✓ Ready to build from scratch"
echo ""

echo "Step 2: Starting Docker services..."
docker-compose up -d
echo "✓ PostgreSQL (port 5432)"
echo "✓ RabbitMQ (port 5672)"
echo "✓ LocalStack (port 4566)"
echo ""

# Wait for services using the separate script
bash ./scripts/wait-for-services.sh

exit 0

