# Trevix — Property Management Platform

A backend REST API powering web, desktop, mobile, and IoT clients for managing properties, tenants, staff, billing, and facilities — built for multi-property landlords and property managers.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17+ |
| Framework | Spring Boot |
| Auth | JWT (Bearer Token) |
| Database | PostgreSQL / MySQL |
| Build Tool | Maven |

---

## Features

- **Multi-role auth** — SuperAdmin, Admin, Staff, Tenant
- **Property & Room management** — status tracking, floor plans
- **Tenant management** — leases, renewals, scorecards
- **Billing** — monthly bills, payments, utility meter readings
- **Maintenance** — requests, photos, priority levels
- **Announcements** — reactions, read receipts, target types
- **Access control** — RFID tags, key checkouts, visitor logs
- **Incidents & SOS alerts** — reporting with severity levels
- **Notifications** — per-user, typed
- **Staff** — profiles, property assignments, activity logs
- **Packages** — delivery tracking per tenant

---

## Project Structure

```
src/
├── controller/       # REST endpoints per role
├── service/          # Business logic
├── repository/       # JPA repositories + custom query impls
├── entity/           # JPA entities
├── dto/
│   ├── request/      # Incoming payloads
│   └── response/     # Outgoing responses
├── enums/            # Domain enumerations
├── mapper/           # Entity ↔ DTO mapping
├── security/         # JWT + UserDetails
├── config/           # App configuration
└── exception/        # Global exception handler + error codes
```

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL (or MySQL)

### Setup

```bash
# Clone the repo
git clone https://github.com/LumnaireDev/trevix.git
cd trevix

# Configure your DB in
src/main/resources/application.properties

# Run
./mvnw spring-boot:run
```

### application.properties (minimum)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/trevix
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

jwt.secret=your_jwt_secret
jwt.expiration=86400000
```

---

## API Overview

| Module | Base Path |
|---|---|
| Auth | `/api/auth` |
| Super Admin | `/api/super-admin` |
| Admin | `/api/admin` |
| Staff | `/api/staff` |
| Tenant | `/api/tenant` |
| Property | `/api/properties` |

All protected endpoints require:
```
Authorization: Bearer <token>
```

---

## Development

```bash
# Run tests
./mvnw test

# Package
./mvnw clean package
```

---

## Team

| Name | Role |
|---|---|
| **Nathaniel Coronacion** | Software Engineer / Backend Developer |
| **Johanna Panganuron** | Web Frontend Developer |
| **Andrie Candelaria** | Mobile App Developer |
| **JL Romero Juanitas** | Desktop App Developer |
| **Jasmine Kate** | IoT Integration / Embedded Software Engineer |
| **Lenhard Pedro Malana** | UI/UX Designer |

---

## License

Private — Lumnaire Dev. All rights reserved.
