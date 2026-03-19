# 📋 Task Manager — Backend Learning Project

## 🎯 Purpose & Intention

This project exists for **one reason**: to learn backend development by doing.

The goal is to build a **fully functional, real-world Task Manager API** using **Java Spring Boot** that covers **every essential backend concept** a developer needs to know. By reading and understanding this codebase, you should be able to confidently call yourself a **proficient backend developer**.

## 📚 Learning Philosophy

> **"Simple. Clear. No shortcuts."**

- Every file has **school-level comments** explaining WHAT, WHY, and HOW
- No Lombok — all getters, setters, and constructors are written manually so you can see everything
- No magic — every annotation is explained
- Code reads like a **textbook**, not a puzzle

## 🏗️ Tech Stack

| Technology                | Purpose                         |
| ------------------------- | ------------------------------- |
| **Java 17**               | Programming language            |
| **Spring Boot 3.2**       | Backend framework               |
| **MongoDB**               | NoSQL database                  |
| **Spring Security + JWT** | Authentication & Authorization  |
| **Spring Cache**          | Caching layer                   |
| **Gradle**                | Build tool & dependency manager |
| **Angular**               | Frontend (separate folder)      |

## 🧠 Backend Concepts Covered

| #   | Concept                          | What You'll Learn                                     |
| --- | -------------------------------- | ----------------------------------------------------- |
| 1   | **Project Structure**            | How to organize a backend project (layers)            |
| 2   | **Models / Entities**            | How to represent database tables as Java classes      |
| 3   | **Repositories**                 | How to access the database without writing queries    |
| 4   | **DTOs (Data Transfer Objects)** | How to separate API input/output from database models |
| 5   | **Validation**                   | How to validate user input (@NotBlank, @Size, etc.)   |
| 6   | **Service Layer**                | Where to put business logic (not in controllers!)     |
| 7   | **Controllers (REST API)**       | How to create HTTP endpoints (GET, POST, PUT, DELETE) |
| 8   | **Global Exception Handling**    | How to handle errors consistently across the app      |
| 9   | **Custom Exceptions**            | How to create your own error types                    |
| 10  | **Authentication (JWT)**         | How to implement login/register with tokens           |
| 11  | **Authorization (Roles)**        | How to restrict access based on user roles            |
| 12  | **Pagination & Sorting**         | How to handle large datasets efficiently              |
| 13  | **Caching**                      | How to speed up your app by caching frequent queries  |
| 14  | **CORS Configuration**           | How to allow frontend apps to call your API           |
| 15  | **Logging**                      | How to track what your application is doing           |
| 16  | **Configuration (Properties)**   | How to configure your app without changing code       |

## 📁 Project Structure

```
backend/
├── src/main/java/com/enterprise/taskmanager/
│   ├── TaskManagerApplication.java     ← App entry point
│   ├── model/                          ← Database models (Task, User)
│   ├── repository/                     ← Database access layer
│   ├── dto/                            ← Request/Response DTOs
│   ├── service/                        ← Business logic
│   ├── controller/                     ← REST API endpoints
│   ├── exception/                      ← Error handling
│   ├── security/                       ← JWT + Spring Security
│   └── config/                         ← App configuration (Cache, CORS)
├── src/main/resources/
│   └── application.properties          ← App settings
└── build.gradle                        ← Dependencies
```

## 🚀 How to Run

### Prerequisites

1. **Java 17** — Install from [Adoptium](https://adoptium.net/)
2. **MongoDB** — Install and make sure it's running on port `27017`

### Start the Backend

```bash
cd backend
gradlew.bat bootRun
```

The API will be available at: `http://localhost:8080`

### API Endpoints

#### Auth (No login required)

| Method | Endpoint             | Description             |
| ------ | -------------------- | ----------------------- |
| POST   | `/api/auth/register` | Register a new user     |
| POST   | `/api/auth/login`    | Login and get JWT token |

#### Tasks (Login required — send JWT token in header)

| Method | Endpoint                 | Description                     |
| ------ | ------------------------ | ------------------------------- |
| POST   | `/api/tasks`             | Create a new task               |
| GET    | `/api/tasks`             | Get all tasks (with pagination) |
| GET    | `/api/tasks/{id}`        | Get a task by ID                |
| PUT    | `/api/tasks/{id}`        | Update a task completely        |
| PATCH  | `/api/tasks/{id}/status` | Update only the status          |
| DELETE | `/api/tasks/{id}`        | Delete a task                   |

## 📝 Project History & Updates

### March 19, 2026 — Major Redesign

- **Requirement**: Redesign the backend to cover ALL basic backend concepts for learning
- **Intention**: Make the code simple, school-level, heavily commented, and fully functional
- **Goal**: After reading this code, be able to build any backend API confidently
- **Changes**: Added JWT auth, pagination, caching, removed Lombok, added comprehensive comments
- **Motto**: _"If you can understand this project, you can build anything."_
