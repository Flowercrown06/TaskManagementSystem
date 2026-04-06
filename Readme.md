# 🚀 Advanced Task Management System API

[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.7-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-latest-blue?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)

A professional, secure, and scalable Task Management System built with **Spring Boot 2.7.7**. This system goes beyond simple CRUD operations, implementing complex business logic like **automated review workflows**, **role-based security**, and **automated notification systems**.

---

## 🌟 Key Features

* **🔐 Advanced Security**: Full JWT (JSON Web Token) implementation with granular Role-Based Access Control (**USER**, **ADMIN**, **SUPER_ADMIN**).
* **📋 Task Life Cycle**: Automated task transitions (Created -> In Progress -> Review -> Done).
* **⚖️ Review System**: Integrated workflow where Users submit tasks and Admins provide structured feedback/approvals.
* **📧 Notification Engine**: Automated Email and System notifications for task assignments and status updates.
* **⏰ Smart Scheduling**: Background jobs to monitor task deadlines and system health.
* **🖼️ QR Code Integration**: Dynamic QR code generation for task details or user verification.
* **🛡️ Robust Error Handling**: Centralized Global Exception Handling with custom business exceptions.
* **🐳 Production Ready**: Multi-stage Docker optimization for lightweight containerization.

---

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Backend** | Java 17, Spring Boot 2.7.7 |
| **Persistence** | Spring Data JPA, Hibernate, PostgreSQL |
| **Security** | Spring Security, JWT, OAuth 2.0 Integration |
| **Mapping** | MapStruct (Performance-optimized DTO mapping) |
| **Validation** | Hibernate Validator (Bean Validation) |
| **Documentation** | Swagger / OpenAPI 3.0 |
| **Utilities** | Lombok, ZXing (QR Code), JavaMail |
| **DevOps** | Docker, Docker Compose, Gradle |

---

## 🏗 Project Architecture

The project follows a strict **layered architecture** to ensure maintainability and separation of concerns:

1.  **Controller Layer**: Handles REST requests and DTO validation.
2.  **Service Layer**: Encapsulates core business logic, logging, and security checks.
3.  **Repository Layer**: Efficient data access using Spring Data JPA.
4.  **Security Component**: Custom JWT filters and authentication providers.
5.  **Mapper Layer**: High-performance entity-to-DTO conversions via MapStruct.

---

## 🧪 Testing

The system includes a comprehensive suite of tests to ensure reliability:

* **Unit Testing**: Core business logic and service methods are tested using **JUnit 5** and **Mockito**.
* **Integration Testing**: API endpoints and database interactions are verified to work together seamlessly.
* **Run Tests**:
    ```bash
    ./gradlew test
    ```

---

## 🚀 Getting Started

### Prerequisites
* Java 17
* Gradle
* Docker & Docker Compose
* PostgreSQL (Render or Local)

### Environment Configuration
Create a `.env` file in the root directory and configure the following variables (refer to `.env.example`):
```env
DATABASE_URL=jdbc:postgresql://your-db-url?ssl=true&sslmode=require
DATABASE_USERNAME=your_db_user
DATABASE_PASSWORD=your_db_password
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email
SPRING_MAIL_PASSWORD=your_app_password
PORT=9090