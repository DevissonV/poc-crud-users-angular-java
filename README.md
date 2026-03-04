# CRUD Users - Full-Stack Monorepo

Aplicación full-stack para gestión de usuarios con **Angular 18** (frontend) y **Spring Boot 3 + SQLite** (backend), siguiendo **Clean Architecture** y completamente dockerizada para máxima portabilidad.

## 🏗️ Arquitectura del Proyecto

Este es un **monorepo** profesional con separación clara entre frontend y backend:

```
crud-users-monorepo/
├── frontend/           # Angular 18 (SPA)
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── README.md
├── backend/            # Spring Boot 3 + SQLite (REST API)
│   ├── src/
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
├── docker-compose.yml  # Orquestación completa
└── README.md          # Este archivo
```

### Diagrama de Arquitectura

```
┌─────────────────┐
│   Frontend      │
│   (Angular 18)  │
│   Port: 80      │
│   + Nginx       │
└────────┬────────┘
         │ HTTP/REST
         ▼
┌─────────────────┐
│   Backend       │
│   (Spring Boot) │
│   Port: 8080    │
│   + Clean Arch  │
└────────┬────────┘
         │ JPA
         ▼
┌─────────────────┐
│   SQLite DB     │
│   (users.db)    │
│   Volume persis.│
└─────────────────┘
```

## 🚀 Quick Start con Docker

**Requisito único:** Docker + Docker Compose instalados

### Levantar toda la aplicación

```bash
# Clonar repositorio
git clone <repo-url>
cd crud-users-monorepo

# Construir y ejecutar con Docker Compose
docker-compose up --build

# O en modo detached (segundo plano)
docker-compose up -d --build
```

### Acceder a la aplicación

- **Frontend (Angular)**: http://localhost
- **Backend API**: http://localhost:8080/api/users
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## 💻 Desarrollo Local (sin Docker)

### Backend (Spring Boot)

```bash
cd backend

# Ejecutar con Maven wrapper
./mvnw spring-boot:run

# Ejecutar tests
./mvnw test
```

**Requisitos:** Java 21 (JDK) + Maven 3.9+

**URL Backend local:** http://localhost:8080

### Frontend (Angular)

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar servidor de desarrollo
npm start

# Ejecutar tests
npm test
```

**Requisitos:** Node.js 20+ + npm 10+

**URL Frontend local:** http://localhost:4200

## 📋 Documentación Completa

- [📖 README del Backend](backend/README.md) - Clean Architecture, API endpoints
- [📖 README del Frontend](frontend/README.md) - Arquitectura Angular, componentes
- [📊 Swagger UI](http://localhost:8080/swagger-ui.html) - Documentación interactiva API

## 🏛️ Arquitectura Técnica

### Backend - Clean Architecture

4 capas independientes:
1. **Domain**: Entidades puras sin dependencias
2. **Application**: DTOs, casos de uso, lógica de negocio
3. **Infrastructure**: JPA, SQLite, configuraciones
4. **Presentation**: REST controllers, excepciones

### Frontend - Angular Standalone

- Angular 18 con Standalone Components
- Signals para reactividad
- TypeScript estricto
- SCSS modular

### Base de Datos - SQLite

- BD embebida en archivo único
- Cero configuración
- Persistencia en Docker volumes

## 🧪 Testing

```bash
# Backend
cd backend && ./mvnw test

# Frontend
cd frontend && npm test
```

## 📦 Tecnologías

**Backend:** Java 21, Spring Boot 3, SQLite, Maven
**Frontend:** Angular 18, TypeScript, Signals, SCSS
**DevOps:** Docker, Docker Compose, Nginx

## 📄 Licencia

MIT License

---

**Stack:** Angular 18 + Spring Boot 3 + SQLite + Docker + Clean Architecture
