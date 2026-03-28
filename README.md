# account-service

A **Spring Boot** microservice for banking account management, featuring JWT-based authentication, Redis-backed distributed locking, and PostgreSQL persistence.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [API Endpoints](#api-endpoints)
- [Security](#security)
- [Building & Testing](#building--testing)

---

## Overview

`account-service` is a RESTful microservice built as part of a banking system. It handles account-related operations with secure access control, data validation, and distributed-safe processing via Redis.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5.x |
| Language | Java 17 |
| Database | PostgreSQL |
| Caching / Locking | Redis (via Redisson) |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| ORM | Spring Data JPA |
| Validation | Spring Validation |
| Observability | Spring Actuator |
| Build Tool | Maven (Maven Wrapper included) |
| Containerisation | Docker (eclipse-temurin:17-jdk-jammy) |
| Utilities | Lombok |

---

## Prerequisites

- Java 17+
- Maven 3.8+ (or use the included `./mvnw`)
- PostgreSQL instance
- Redis instance

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/apramania/account-service.git
cd account-service
```

### 2. Configure environment

Set the following in `src/main/resources/application.properties` (or via environment variables):

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password

# Redis
spring.redis.host=localhost
spring.redis.port=6379

# JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000
```

### 3. Build and run

```bash
./mvnw spring-boot:run
```

The service will start on `http://localhost:8080` by default.

---

## Running with Docker

### Build the JAR first

```bash
./mvnw clean package -DskipTests
```

### Build the Docker image

```bash
docker build -t account-service .
```

### Run the container

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/your_db \
  -e SPRING_DATASOURCE_USERNAME=your_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  account-service
```

> **Tip:** Use Docker Compose to orchestrate the service together with PostgreSQL and Redis in a local environment.

---

## Configuration

Key configuration properties:

| Property | Description | Default |
|---|---|---|
| `server.port` | HTTP port | `8080` |
| `spring.datasource.url` | PostgreSQL JDBC URL | — |
| `spring.datasource.username` | DB username | — |
| `spring.datasource.password` | DB password | — |
| `spring.redis.host` | Redis host | `localhost` |
| `spring.redis.port` | Redis port | `6379` |
| `jwt.secret` | JWT signing key | — |
| `jwt.expiration` | Token TTL (ms) | — |

---

## Project Structure

```
account-service/
├── src/
│   ├── main/
│   │   ├── java/com/apratim/banking/accountservice/
│   │   │   ├── controller/      # REST controllers (AccountController, ...)
│   │   │   ├── service/         # Business logic (AccountService, TransactionService, ...)
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── model/           # JPA entities
│   │   │   ├── dto/             # DTOs (CreateAccountRequest, TransferRequest,
│   │   │   │                    #        AccountResponse, TransactionResponse, ...)
│   │   │   ├── config/          # Security & app configuration
│   │   │   └── security/        # JWT filters & utilities
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit & integration tests
├── Dockerfile
├── pom.xml
└── mvnw / mvnw.cmd
```

---

## API Endpoints

All account endpoints are prefixed with `/api/v1/accounts`. Pass the JWT as a Bearer token in the `Authorization` header for protected routes.

| Method | Path | Description | Notes |
|---|---|---|---|
| `POST` | `/api/v1/accounts` | Create a new account | Body: `CreateAccountRequest` |
| `GET` | `/api/v1/accounts/{accountNumber}` | Get account details by account number | |
| `POST` | `/api/v1/accounts/transfer` | Transfer money between accounts | Requires `Idempotency-Key` header; body: `TransferRequest` |
| `GET` | `/api/v1/accounts/{accountNumber}/transactions` | List paginated transactions for an account | Supports Spring `Pageable` params |
| `PATCH` | `/api/v1/accounts/{accountNumber}/freeze` | Freeze an account | |
| `PATCH` | `/api/v1/accounts/{accountNumber}/unfreeze` | Unfreeze an account | |
| `GET` | `/actuator/health` | Health check | |

### Idempotency

The `/transfer` endpoint requires an `Idempotency-Key` request header to safely deduplicate repeated transfer requests (e.g. due to retries).

### Pagination

The `/transactions` endpoint supports standard Spring Data pagination query params: `page`, `size`, and `sort`. Example:

```
GET /api/v1/accounts/ACC123/transactions?page=0&size=20&sort=createdAt,desc
```

---

## Security

- Authentication is implemented with **Spring Security** and stateless **JWT** tokens.
- Tokens are signed with a configurable secret key and carry an expiry.
- Distributed operations (e.g. balance updates) are protected with **Redisson** distributed locks to prevent race conditions in a multi-instance deployment.
- Passwords are stored securely using Spring Security's `PasswordEncoder`.

---

## Building & Testing

```bash
# Run all tests
./mvnw test

# Build JAR (skip tests)
./mvnw clean package -DskipTests

# Build JAR with tests
./mvnw clean package
```

The packaged JAR will be at `target/account-service-0.0.1-SNAPSHOT.jar`.

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m 'Add your feature'`
4. Push to the branch: `git push origin feature/your-feature`
5. Open a Pull Request

---
