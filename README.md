## 🔐 Auth Backend – Spring Boot Microservice

This is a standalone authentication backend responsible for user management and JWT token handling. It serves as the security gateway for the main application backend.

---

### 📌 Features

* ✅ User Registration
* 🔐 User Login with JWT issuance
* ✏️ User Update
* 🗑️ User Deletion
* ✅ Token Validation for other services (e.g., Main Backend)

---

### 📣 API Endpoints (documented with Swagger)

Visit Swagger UI: `http://localhost:8001/swagger-ui.html`

---

### 🛡️ Security

* Uses **JWT (JSON Web Tokens)** for stateless authentication.
* All secure routes require `Authorization: Bearer <token>` header.

---

### 🔄 Token Validation Workflow

1. Frontend sends a JWT token to `/validate`.
2. Auth backend decodes and checks the signature.
3. On success, returns `{ userId, name }`.
4. Main backend uses this identity to proceed.

---

### ⚙️ Tech Stack

* Java 21
* Spring Boot 3.4
* Spring Security
* Spring Data JPA
* PostgreSQL
* Lombok
* Swagger / OpenAPI
