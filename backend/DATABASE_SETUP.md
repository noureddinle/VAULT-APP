# Database Configuration Guide

## Problem Fixed

The Spring Boot application was failing to start with the error:
```
Failed to configure a DataSource: 'url' attribute is not specified and no embedded datasource could be configured.
```

## Solution Implemented

### 1. Added PostgreSQL Database Configuration
- Updated `src/main/resources/application.properties` with PostgreSQL connection settings
- Configured to connect to the PostgreSQL database defined in `docker-compose.yml`
- Uses environment variables with default fallback values

### 2. Added H2 In-Memory Database for Tests
- Added H2 dependency to `pom.xml` (test scope only)
- Created `src/test/resources/application-test.properties` for test configuration
- Updated test class to use `@ActiveProfiles("test")` annotation

### 3. Configuration Details

#### Production Database (PostgreSQL)
The application connects to PostgreSQL with these settings:
- **URL**: `jdbc:postgresql://localhost:5432/vaultdb`
- **Username**: `vaultuser`
- **Password**: `vaultpass`
- **Auto DDL**: `update` (creates/updates tables automatically)

#### Test Database (H2)
Tests use an in-memory H2 database:
- **URL**: `jdbc:h2:mem:testdb`
- **Auto DDL**: `create-drop` (fresh database for each test run)

## Running the Application

### 1. Start the PostgreSQL Database
```bash
docker-compose up -d
```

### 2. Run the Application
```bash
cd backend
.\mvnw.cmd spring-boot:run
```

### 3. Run Tests (No PostgreSQL Required)
```bash
cd backend
.\mvnw.cmd test
```

## Environment Variables

You can customize database settings using environment variables:
- `POSTGRES_DB` - Database name (default: vaultdb)
- `POSTGRES_USER` - Database user (default: vaultuser)
- `POSTGRES_PASSWORD` - Database password (default: vaultpass)

## Verifying the Setup

✅ Tests are passing (using H2)
✅ Database configuration is in place
✅ Docker Compose is configured for PostgreSQL

## Next Steps

1. Start the PostgreSQL database: `docker-compose up -d`
2. Run the application: `.\mvnw.cmd spring-boot:run`
3. The application will be available at: `http://localhost:8080`

## Security Note

⚠️ The application generates a random security password on startup. Look for this line in the console:
```
Using generated security password: [random-password]
```

This is for development only. Configure proper security before deploying to production.

