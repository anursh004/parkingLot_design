# Parking Lot Application

A full-stack Spring Boot sample that demonstrates SOLID principles, design patterns (Factory, Strategy, Repository, and Configuration), structured exception handling, distributed logging, and OTP flows.

## ✅ Features
- **SOLID-aligned architecture** with interfaces and dependency injection.
- **Design patterns**:
  - **Factory** for vehicle creation.
  - **Strategy** for pricing calculations.
  - **Repository** for ticket persistence.
- **Exception handling** with global API errors.
- **Distributed logging** using trace/request IDs.
- **OTP flow** with secure hashing and attempt limits.
- **Security configuration** with basic auth and secure headers.
- **UI** served from `/static/index.html`.

## 🚀 Run the Application

```bash
mvn spring-boot:run
```

Open: <http://localhost:8080>

Default credentials:
- **Username:** `parking-admin`
- **Password:** `parking-secret`

## 🔐 OTP Flow
`POST /api/otp/request`
```json
{ "principal": "driver@example.com" }
```

`POST /api/otp/verify`
```json
{ "principal": "driver@example.com", "otp": "123456" }
```

> For demo purposes the OTP is returned in the response. In production, send OTPs through email/SMS and never expose them via APIs.

## 📌 Design Notes
- The service layer avoids static state and uses dependency injection.
- Logging includes trace and request IDs for distributed tracing.
- OTP values are hashed before being stored.
