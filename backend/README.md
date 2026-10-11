# SmartBite Backend – Spring Boot REST API

Smart Campus Canteen Management System backend built with **Java 21**, **Spring Boot 3.3.4 / 3.4**, **Spring Security 6**, **JPA/Hibernate**, and **MySQL 8.4**.

---

## 🛠️ Technology Stack
- **Language**: Java 21 (LTS)
- **Framework**: Spring Boot 3.3.4 / 3.4
- **Security**: Spring Security 6, JJWT 0.12.6, BCrypt (Strength 10)
- **Database**: MySQL 8.4 LTS / MySQL 8.0 with HikariCP
- **ORM / Persistence**: Spring Data JPA & Hibernate 6
- **Build Tool**: Maven 3.9+
- **Testing**: JUnit 5, Spring Security Test, MockMvc

---

## 📂 Architecture Overview
```text
backend/
├── pom.xml
├── .env.example
├── .gitignore
├── README.md
└── src/
    ├── main/
    │   ├── java/com/smartbite/
    │   │   ├── SmartBiteApplication.java
    │   │   ├── config/          # Cors, DataSeeder, GlobalExceptionHandler
    │   │   ├── controller/      # Auth, Admin, Menu, Order, Health REST Controllers
    │   │   ├── dto/             # Request & Response Transfer Objects
    │   │   ├── model/           # JPA Entities (User, Order, OrderItem, MenuItem, Role)
    │   │   ├── repository/      # Spring Data JPA Repositories
    │   │   ├── security/        # JWT Provider, Auth Filter, EntryPoints, SecurityConfig
    │   │   └── service/         # Business logic layer
    │   └── resources/
    │       ├── application.properties
    │       └── application-local.properties (local overrides, ignored by Git)
    └── test/
        └── java/com/smartbite/  # Comprehensive unit and integration test suite
```

---

## 🚀 Running Locally

### 1. Environment Setup
Copy `.env.example` to `.env` and set your database credentials:
```bash
cp .env.example .env
```
Or create `src/main/resources/application-local.properties` with:
```properties
spring.datasource.password=YourDatabasePassword
security.jwt.secret=YourSecureJwtSecretKeyAtLeast256BitsLong12345!
smartbite.admin.password=YourSecureAdminPassword
```

### 2. Build and Run Tests
```bash
mvn clean test
```

### 3. Start Backend
```bash
mvn spring-boot:run
```
The REST API will be available at `http://localhost:8080/api`.

---

## 📡 REST Endpoints

### Public
- `GET /api/health`: System health status
- `GET /api/menu`: Browse food menu items
- `POST /api/auth/register`: Student registration
- `POST /api/auth/login`: Student / Admin login
- `POST /api/auth/logout`: Clears auth cookie

### Authenticated Student
- `GET /api/auth/me`: Current student profile
- `POST /api/orders`: Place food order with server-calculated totals
- `GET /api/orders/my`: Scoped private order history & pickup tokens

### Authenticated Admin (`ROLE_ADMIN`)
- `GET /api/admin/orders`: Live kitchen order queue
- `PATCH /api/admin/orders/{id}/status`: Update preparation status (`PENDING` -> `PREPARING` -> `READY` -> `COMPLETED`)
- `PATCH /api/admin/menu/{id}/availability`: Toggle food availability
- `GET /api/admin/stats`: Kitchen dashboard analytics
