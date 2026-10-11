# SmartBite — Smart Campus Canteen Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.4%20LTS-blue.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](#)

SmartBite is a full-stack smart campus canteen management web application built to eliminate long cafeteria queues, provide live menu ordering, dispatch real-time digital pickup tokens, and deliver secure role-based kitchen operations.

---

## 📑 Table of Contents
1. [Project Overview](#-project-overview)
2. [Key Features](#-key-features)
3. [Technology Stack](#-technology-stack)
4. [Prerequisites](#-prerequisites)
5. [Database Setup (MySQL)](#-database-setup-mysql)
6. [Environment Variables & Configuration](#-environment-variables--configuration)
7. [Running the Application](#-running-the-application)
   - [Starting the Spring Boot Backend](#1-starting-the-spring-boot-backend)
   - [Starting the Frontend Client](#2-starting-the-frontend-client)
8. [API Overview & Role Authorization](#-api-overview--role-authorization)
9. [Automated Testing](#-automated-testing)
10. [Screenshots & Visual Tour Guidance](#-screenshots--visual-tour-guidance)
11. [Security & Best Practices](#-security--best-practices)

---

## 🌟 Project Overview
On university and college campuses, meal hours are characterized by crowded counters, long wait times, lost paper receipts, and kitchen congestion. **SmartBite** modernizes campus dining through:
- **Instant Student Self-Ordering**: Browse daily dishes categorized by Breakfast, Meals, Snacks, and Beverages.
- **Dietary & Search Filters**: Instant client-side and server-side filtering for Pure Veg, Non-Veg, prep times, and dish ratings.
- **Automated Digital Pickup Tokens**: Every placed order receives a unique token (e.g., `#A-101`) to eliminate physical tokens.
- **Role-Secured Kitchen Dashboard**: Real-time kitchen queue where authorized canteen managers advance order preparation statuses (`PENDING` → `PREPARING` → `READY` → `COMPLETED`) and toggle live food stock availability.
- **Secure Authentication**: BCrypt-hashed password storage, stateless JWT authentication with dual extraction (HttpOnly cookie + Bearer header), and strict server-side role authorization (`ROLE_STUDENT` vs `ROLE_ADMIN`).

---

## 🚀 Key Features

### For Students
- **Student Registration & Login**: Sign up with college roll number, name, and email; sign in using roll number or email.
- **Dynamic Canteen Menu**: Real-time pricing, dish descriptions, prep times, and availability tags loaded from MySQL.
- **Cart & Order Checkout**: Interactive slide-over cart drawer with quantity adjustments and server-validated pricing.
- **Pickup Token Confirmation**: Instant order confirmation with token code and live item summary.
- **Private "My Orders" Tracker**: Authenticated students can view only their own active and historical orders.

### For Kitchen Staff & Admins
- **Restricted Admin Portal**: Fully protected via Spring Security server-side authorization (`ROLE_ADMIN`).
- **Live Kitchen Queue**: Real-time order monitoring with color-coded status badges.
- **Kitchen Order Processing**: One-click status transitions (`Start Preparing`, `Mark Ready`, `Complete / Picked Up`).
- **Live Menu Availability Switch**: Toggle dishes between `Available` and `Out of Stock` instantly across the campus client.
- **Operational Metrics**: Real-time counters for Total Orders, Pending Orders, In-Kitchen, Ready for Pickup, and Today's Volume.

---

## 🛠 Technology Stack

| Layer | Technologies Used |
|---|---|
| **Frontend** | HTML5 Semantic markup, Custom Vanilla CSS3 (CSS Variables, Flexbox/Grid, Glassmorphism), Vanilla ES6+ JavaScript |
| **Backend** | Java 21, Spring Boot 3.3.4 / 3.4, Spring Security 6, Spring Data JPA / Hibernate, Maven |
| **Security** | JSON Web Tokens (JJWT 0.12.6), BCrypt Password Hashing (Strength 10), Stateless Filters, HttpOnly Cookies, CORS |
| **Database** | MySQL 8.4 LTS / MySQL 8.0, HikariCP Connection Pool |
| **Testing** | JUnit 5, Spring Security Test, MockMvc, AssertJ |

---

## 📋 Prerequisites
Ensure the following tools are installed on your machine:
- **Java Development Kit (JDK)**: Java 21 or Java 17 ([Download OpenJDK / Oracle](https://www.oracle.com/java/technologies/downloads/))
- **Apache Maven**: Version 3.8+ or 3.9+ ([Download Maven](https://maven.apache.org/download.cgi))
- **MySQL Server**: MySQL 8.0+ or MySQL 8.4 LTS ([Download MySQL](https://dev.mysql.com/downloads/mysql/))
- **Python 3** (or Node.js `http-server` / VS Code Live Server) to serve the static frontend on port 3000.

---

## 🗄 Database Setup (MySQL)

1. **Start MySQL Server**: Ensure your MySQL service is active on port `3306`.
2. **Log in to MySQL CLI or Workbench**:
   ```bash
   mysql -u root -p
   ```
3. **Execute Schema & Database Setup**:
   You can run the provided database schema script at [`database/schema.sql`](file:///C:/SmartBite/database/schema.sql):
   ```sql
   CREATE DATABASE IF NOT EXISTS smartbite CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   CREATE USER IF NOT EXISTS 'smartbite_user'@'localhost' IDENTIFIED BY 'YourStrongPasswordHere!';
   GRANT ALL PRIVILEGES ON smartbite.* TO 'smartbite_user'@'localhost';
   FLUSH PRIVILEGES;
   ```
4. **Import Schema & Seed Menu Items**:
   ```bash
   mysql -u smartbite_user -p smartbite < database/schema.sql
   ```
   *(Note: Spring Data JPA will also automatically create or update tables on application startup via `spring.jpa.hibernate.ddl-auto=update`.)*

---

## ⚙ Environment Variables & Configuration

SmartBite reads all sensitive credentials and secrets from environment variables or a local `.env` file that is **strictly excluded from Git**.

### Template File (`.env.example`)
Review the root template [`.env.example`](file:///C:/SmartBite/.env.example):
```dotenv
# Server Configuration
SERVER_PORT=8080

# MySQL Database Configuration
DB_HOST=localhost
DB_PORT=3306
DB_NAME=smartbite
DB_USERNAME=smartbite_user
DB_PASSWORD=your_mysql_database_password_here

# Security & JWT Signing Secret (HMAC-SHA256, minimum 32 characters)
JWT_SECRET=your_secure_random_jwt_secret_key_minimum_256_bits_here_12345!
JWT_EXPIRATION_MS=86400000

# Initial Admin Account (Created automatically on first boot if absent)
SMARTBITE_ADMIN_NAME=Campus Canteen Administrator
SMARTBITE_ADMIN_EMAIL=admin@smartbite.campus
SMARTBITE_ADMIN_ROLL=ADMIN-01
SMARTBITE_ADMIN_PASSWORD=your_secure_initial_admin_password_here
```

### Safe Local Setup
1. Copy `.env.example` to `backend/.env` (or create `backend/src/main/resources/application-local.properties`).
2. Fill in your local database password and a secure JWT secret.
3. Both `.env` and `application-local.properties` are listed in `.gitignore` and will never be tracked or uploaded.

---

## 🚀 Running the Application

### 1. Starting the Spring Boot Backend
Open a terminal in the `backend/` directory:

```bash
cd backend
mvn spring-boot:run
```
*(Or in Windows PowerShell: `.\mvnw.cmd spring-boot:run`)*

- The backend REST API will start on: `http://localhost:8080`
- API Health Check: `http://localhost:8080/api/health`
- Dynamic Menu: `http://localhost:8080/api/menu`

### 2. Starting the Frontend Client
In a separate terminal in the project root:

```bash
# Using Python built-in server:
python -m http.server 3000
```
*(Or use `npx serve . -p 3000` or VS Code Live Server)*

Open your browser and navigate to:
**[http://localhost:3000](http://localhost:3000)**

---

## 📡 API Overview & Role Authorization

All endpoints reside under `/api`. Server-side Spring Security enforces authentication and role boundaries:

| Method | Endpoint | Authorization | Description |
|---|---|---|---|
| `GET` | `/api/health` | Public | System status and service metadata |
| `GET` | `/api/menu` | Public | Browse campus dishes and drink options |
| `POST` | `/api/auth/register` | Public | Register new student (`ROLE_STUDENT`) |
| `POST` | `/api/auth/login` | Public | Sign in using Roll No / Email + Password |
| `POST` | `/api/auth/logout` | Public | Clears HttpOnly session cookie |
| `GET` | `/api/auth/me` | Authenticated | Fetch current user profile |
| `POST` | `/api/orders` | Authenticated | Place canteen order (server assigns student ID) |
| `GET` | `/api/orders/my` | Authenticated | Fetch orders scoped to current student |
| `GET` | `/api/orders/{id}` | Authenticated | View order details (scoped to owner/admin) |
| `GET` | `/api/admin/orders` | **ROLE_ADMIN** | Live kitchen order queue (newest first) |
| `PATCH` | `/api/admin/orders/{id}/status` | **ROLE_ADMIN** | Update preparation status |
| `PATCH` | `/api/admin/menu/{id}/availability` | **ROLE_ADMIN** | Toggle item stock in real time |
| `GET` | `/api/admin/stats` | **ROLE_ADMIN** | Real-time kitchen operations statistics |

---

## 🧪 Automated Testing

SmartBite includes a test suite covering domain entities, repositories, authentication filters, role security, order scoping, and menu management.

Run the test suite via Maven:
```bash
cd backend
mvn test
```

### Test Coverage Highlights
- **`AuthControllerTest`** (9 tests): Registration, BCrypt password hashing verification, duplicate roll/email rejection, login validation, wrong-password generic error, token expiration, profile retrieval, and logout.
- **`AdminControllerTest`** (12 tests): Unauthenticated access rejection (401), student access rejection (403), admin authorization (200), order status state transitions, and stock toggling.
- **`OrderControllerTest`** (10 tests): Order placement, server price recalculation, pickup token uniqueness, customer scoping, and input validation rules.
- **`MenuControllerTest`** & **`HealthControllerTest`**: Menu filtering and health probes.

---

## 📸 Screenshots & Visual Tour Guidance

When publishing or submitting your project, include screenshots in an `assets/` or `docs/images/` folder and link them here:

1. **Homepage & Live Menu**: Hero section with category pills, live availability indicators, and fast search.
2. **Student Authentication Modal**: Clean tabbed interface for student login and roll-number registration.
3. **Cart & Token Confirmation**: Slide-over cart drawer and the pickup token modal (`#TK-101`).
4. **Student Order History**: Scoped My Orders modal displaying active tokens and preparation statuses.
5. **Kitchen & Admin Dashboard**: Metrics bar, live kitchen queue with transition buttons (`Start Preparing`, `Mark Ready`, `Complete`), and real-time stock toggles.

---

## 🔒 Security & Best Practices

- **Zero Hardcoded Secrets**: All passwords and JWT secrets are extracted via environment variables.
- **No Plaintext Passwords**: Passwords hashed with BCrypt (10 rounds).
- **No Public Admin Registration**: Admin account creation is restricted to internal seeding with strong credentials.
- **Client Input Isolation**: Food prices and line totals are strictly calculated server-side from the database; client-sent prices are ignored.
- **Server-Derived Identity**: The student's identity is derived from `@AuthenticationPrincipal User currentUser`, preventing user impersonation.
- **Cross-Origin Resource Sharing (CORS)**: Configured strictly for the frontend origin with credentials enabled.

---

## 📄 License
This project is developed for educational and campus canteen management demonstration purposes under the MIT License.
