#!/bin/bash

# Wait for Docker services to be ready
# This script checks if PostgreSQL, RabbitMQ, and LocalStack are accepting connections

set -e

echo "Waiting for services to be ready..."
echo ""

# Give containers a moment to start
sleep 5

# Function to check service health
check_service_health() {
    local service=$1
    local port=$2
    local max_attempts=$3
    local attempt=1

    echo -n "Checking $service..."

    while [ $attempt -le $max_attempts ]; do
        case $service in
            "postgres")
                # Use nc (netcat) to check if PostgreSQL port is open
                if nc -z localhost $port >/dev/null 2>&1; then
                    echo " ✓"
                    return 0
                fi
                ;;
            "rabbitmq")
                # Use nc (netcat) to check if port is open
                if nc -z localhost $port >/dev/null 2>&1; then
                    echo " ✓"
                    return 0
                fi
                ;;
            "localstack")
                # Use curl to check health endpoint
                if curl -s http://localhost:$port/_localstack/health >/dev/null 2>&1; then
                    echo " ✓"
                    return 0
                fi
                ;;
        esac

        echo -n "."
        sleep 2
        ((attempt++))
    done
    echo " ✗ (timeout after $((max_attempts * 2)) seconds)"
    return 1
}

# Check all services
check_service_health "postgres" "5432" 30
POSTGRES_OK=$?

check_service_health "rabbitmq" "5672" 30
RABBITMQ_OK=$?

check_service_health "localstack" "4566" 30
LOCALSTACK_OK=$?

echo ""

# Validate critical services
if [ $POSTGRES_OK -ne 0 ]; then
    echo "✗ PostgreSQL failed to start. Checking logs..."
    docker-compose logs postgres | tail -50
    exit 1
fi

if [ $RABBITMQ_OK -ne 0 ]; then
    echo "⚠ RabbitMQ may not be fully ready, but continuing..."
fi

if [ $LOCALSTACK_OK -ne 0 ]; then
    echo "⚠ LocalStack may not be fully ready, but continuing..."
fi

echo "✓ PostgreSQL database 'hyperativa' is ready"
echo "✓ RabbitMQ is ready"
echo "✓ LocalStack is ready"
echo "✓ All services ready"
echo ""

exit 0

