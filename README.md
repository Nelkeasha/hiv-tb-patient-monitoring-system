# HIV & TB Patient Monitoring System — Backend API

Spring Boot REST API for the HIV & TB Intelligent Patient Monitoring and Early Intervention System at Dream Medical Center, Kigali, Rwanda.

Final year thesis project — Africa Christian University (AUCA)
Author: IGIHOZO Nelly

---

## Technology Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.6 |
| Language | Java 21 |
| Security | Spring Security 7.0.5 + JWT (JJWT 0.12.6) |
| Database | PostgreSQL 16 |
| ORM | Hibernate 7.2.12 / Spring Data JPA |
| Migrations | Flyway 11.14.1 |
| API Docs | springdoc-openapi 3.0.2 (Swagger UI) |
| Build | Maven |

---

## Prerequisites

- Java 21
- PostgreSQL 16 running locally
- Maven 3.9+

---

## Database Setup

Create the database before running the application:

```sql
CREATE DATABASE hivtb_db;
```

The schema is managed entirely by Flyway. All tables and PostgreSQL custom enum types are created automatically on first startup.

---

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hivtb_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

app.jwt.secret=your_jwt_secret_key_minimum_32_characters
app.jwt.expiration=900000
app.jwt.refresh-expiration=604800000
```

> **Security notice:** The `application.properties` committed in this repository contains
> local development credentials only. Before deploying to any shared or production
> environment, replace the database password and JWT secret with strong, unique values.
> Never commit real production credentials to version control.

---

## Running the Application

```bash
mvn spring-boot:run
```

The application starts on `http://localhost:8080`.

On first startup the system automatically seeds:
- Default facility: **Dream Medical Center**
- Default admin account: `admin@hivtb.rw` / `Admin@2026`

---

## API Documentation

Swagger UI is available at:

```
http://localhost:8080/swagger-ui/index.html
```

To test protected endpoints:
1. Call `POST /api/auth/login` with admin or staff credentials
2. Copy the `accessToken` from the response
3. Click **Authorize** in Swagger UI and paste the token
4. All subsequent requests will include the Bearer token automatically

---

## API Endpoints

### Authentication — `/api/auth`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Login and receive JWT tokens |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/logout` | Invalidate refresh token |

### Admin — User Management — `/api/admin/users`
| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/api/admin/users/chw` | SYSTEM_ADMIN | Create a CHW account |
| POST | `/api/admin/users/provider` | SYSTEM_ADMIN | Create a facility provider account |
| POST | `/api/admin/users/supervisor` | SYSTEM_ADMIN | Create a supervisor account |
| GET | `/api/admin/users` | SYSTEM_ADMIN | List all users |
| PUT | `/api/admin/users/{id}/toggle-status` | SYSTEM_ADMIN | Activate or deactivate user |
| PUT | `/api/admin/users/{id}/reset-password` | SYSTEM_ADMIN | Reset user password |

### Admin — Stock Management — `/api/admin/stock`
| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/admin/stock/resupply-requests` | ADMIN / PROVIDER / SUPERVISOR | View pending resupply requests |
| PUT | `/api/admin/stock/{id}/restock` | ADMIN / PROVIDER | Add stock to fulfill resupply |

### CHW — Patients — `/api/chw/patients`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/chw/patients` | Enroll a new patient |
| GET | `/api/chw/patients` | List my assigned patients |
| GET | `/api/chw/patients/{id}` | Get patient details |
| PUT | `/api/chw/patients/{id}` | Update patient information |

### CHW — Home Visits — `/api/chw/visits`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/chw/visits` | Record a home visit |
| GET | `/api/chw/visits/patient/{patientId}` | List visits for a patient |
| GET | `/api/chw/visits/{id}` | Get visit details |

### CHW — Stock — `/api/chw/stock`
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/chw/stock` | Initialize a medication stock item |
| GET | `/api/chw/stock` | View my current stock levels |
| GET | `/api/chw/stock/low` | View stock items below reorder level |
| POST | `/api/chw/stock/dispense` | Dispense medication to a patient |
| PUT | `/api/chw/stock/{id}/request-resupply` | Request medication resupply |

---

## User Roles

| Role | Description |
|---|---|
| `SYSTEM_ADMIN` | Manages user accounts and system configuration |
| `CHW` | Community Health Worker — registers patients, records visits, manages stock |
| `FACILITY_PROVIDER` | Clinical staff — monitors dashboards and receives alerts |
| `SUPERVISOR` | Oversees CHW network and program-level performance |
| `PATIENT` | Mobile app patient — confirms medication doses |

---

## Database Schema

17 tables managed by Flyway:

`system_users` · `facilities` · `chws` · `facility_providers` · `supervisors` · `patients` · `home_visits` · `medication_records` · `confirmation_logs` · `stock_records` · `dispensing_events` · `alerts` · `ai_risk_scores` · `treatment_plans` · `fhir_sync_logs` · `audit_logs` · `refresh_tokens`

---

## System Architecture

```
Flutter Mobile App (offline-first, SQLite)
        ↓ HTTPS / JWT
Spring Boot REST API  ←→  PostgreSQL 16
        ↓
Python AI Microservice (nightly risk scoring)
        ↓
Next.js Web Dashboard (providers & supervisors)
        ↓
HAPI FHIR Gateway (HL7 FHIR R4)
