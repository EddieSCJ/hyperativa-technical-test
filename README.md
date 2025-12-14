# Hyperativa Tech Test - Card Management API

A secure REST API for managing credit card information with JWT authentication, encrypted storage, and batch processing.

## Requirements

- Java 21
- Docker
- Git

## Quick Start

```bash
# Clone the repository
git clone <repository-url>
cd hyperativa-tech-test

# Make the setup script executable
chmod +x setup.sh

# Run the complete setup and start the application
./setup.sh
```

The script will:
1. Start Docker services (PostgreSQL, RabbitMQ, LocalStack)
2. Build the project
3. Run all tests
4. Run Flyway migrations automatically on startup
5. Start the application with local profile

Application runs on: **http://localhost:8080**

## Local Environment

The local profile runs with:
- PostgreSQL database
- Automatic Flyway migrations (core + local development data)
- RabbitMQ message queue
- LocalStack for AWS S3 simulation
- Swagger UI enabled

### Default Admin User (Local Only)

A default admin user is created automatically for local development:

```
Username: admin
Password: Admin123!
```

Use this to test the API immediately after startup.

## Ports

- Application: 8080
- PostgreSQL: 5432
- RabbitMQ: 5672
- LocalStack: 4566

## API Usage

### Swagger Documentation

Once the application is running, access the interactive API documentation:

```
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI Spec: http://localhost:8080/v3/api-docs
```

You can test all endpoints directly from the Swagger UI interface.

### Login with Default Admin

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123!"}'
```

### Register User
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"Pass123!","roleName":"USER"}'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"Pass123!"}'
```

### Create Card
```bash
TOKEN="<jwt-token>"
curl -X POST http://localhost:8080/api/cards \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"cardNumber":"4456897999999999"}'
```

### Lookup Card
```bash
TOKEN="<jwt-token>"
curl -X POST http://localhost:8080/api/cards/lookup \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"cardNumber":"4456897999999999"}'
```

## Running Tests

```bash
# All tests
./gradlew test

# Specific test
./gradlew test --tests CardControllerTest

# View test report
open build/reports/tests/test/index.html
```

## Manual Setup (if not using setup.sh)

```bash
# Start services
docker-compose up -d

# Build project
./gradlew clean build

# Run application with local profile (includes migrations)
./gradlew bootRun --args='--spring.profiles.active=local'
```

## Stop Services

```bash
# Stop application (Ctrl+C in terminal)

# Stop Docker services
docker-compose down
```

## Troubleshooting

**Port 8080 already in use:**
```bash
lsof -i :8080
kill -9 <PID>
```

**Docker services not starting:**
```bash
docker-compose restart
```

**Build errors:**
```bash
./gradlew clean --refresh-dependencies build
```

**Migration failures:**
Check PostgreSQL is running and accessible:
```bash
docker-compose ps
docker logs <postgres-container>
```

