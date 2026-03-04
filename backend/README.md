# Backend - CRUD Users API

API REST para gestión de usuarios construida con **Spring Boot 3** y **Clean Architecture**.

## 🏗️ Arquitectura

Este backend implementa **Clean Architecture** (Arquitectura Hexagonal) con clara separación de responsabilidades:

```
src/main/java/com/crud/users/
├── domain/              # Capa de Dominio (núcleo sin dependencias)
│   ├── model/          # Entidades de dominio puras
│   ├── repository/     # Ports (interfaces de repositorio)
│   └── exception/      # Excepciones de dominio
├── application/         # Capa de Aplicación (casos de uso)
│   ├── dto/            # DTOs de entrada/salida
│   ├── service/        # Servicios de lógica de negocio
│   ├── mapper/         # Mappers Domain ↔ DTO
│   └── validation/     # Validaciones personalizadas
├── infrastructure/      # Capa de Infraestructura (implementaciones)
│   ├── persistence/    # JPA entities, repositories, mappers
│   ├── config/         # Configuración (DB, CORS, OpenAPI)
│   └── validation/     # Validators personalizados
└── presentation/        # Capa de Presentación (API REST)
    ├── controller/     # REST controllers
    └── exception/      # Manejo global de excepciones
```

### Beneficios de Clean Architecture

- **Independencia de frameworks**: El dominio no depende de Spring, JPA, ni SQLite
- **Testabilidad**: Lógica de negocio fácilmente testeable sin infraestructura
- **Flexibilidad**: Cambiar BD (SQLite → PostgreSQL) solo afecta la capa de infraestructura
- **Mantenibilidad**: Responsabilidades claras, código organizado por capas

## 🚀 Tecnologías

- **Java 21** (LTS)
- **Spring Boot 3.2.3**
- **Spring Data JPA** + **Hibernate**
- **SQLite** (base de datos embebida)
- **Bean Validation** (JSR-303)
- **SpringDoc OpenAPI 3** (Swagger UI)
- **Maven 3.9+**

## 📋 Endpoints API

### Usuarios

| Método | Endpoint              | Descripción                    |
|--------|-----------------------|--------------------------------|
| GET    | `/api/users`          | Obtener todos los usuarios     |
| GET    | `/api/users/{id}`     | Obtener usuario por ID         |
| GET    | `/api/users/search?q=` | Buscar usuarios (nombre/email) |
| POST   | `/api/users`          | Crear nuevo usuario            |
| PUT    | `/api/users/{id}`     | Actualizar usuario             |
| DELETE | `/api/users/{id}`     | Eliminar usuario               |

### Ejemplos con curl

**Crear usuario:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan.perez@example.com",
    "telefono": "+57 300 123 4567",
    "fechaNacimiento": "1990-05-15"
  }'
```

**Obtener todos los usuarios:**
```bash
curl http://localhost:8080/api/users
```

**Buscar usuarios:**
```bash
curl "http://localhost:8080/api/users/search?q=juan"
```

**Actualizar usuario:**
```bash
curl -X PUT http://localhost:8080/api/users/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Carlos",
    "apellido": "Pérez López"
  }'
```

**Eliminar usuario:**
```bash
curl -X DELETE http://localhost:8080/api/users/{id}
```

## 🏃 Ejecución Local

### Requisitos

- Java 21 (JDK)
- Maven 3.9+

### Pasos

```bash
cd backend

# Instalar dependencias y ejecutar
./mvnw spring-boot:run

# O si tienes Maven instalado globalmente
mvn spring-boot:run
```

La API estará disponible en:
- **API Base**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## 🐳 Ejecución con Docker

### Construir imagen

```bash
cd backend
docker build -t crud-users-api .
```

### Ejecutar contenedor

```bash
docker run -d \
  -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  --name crud-users-api \
  crud-users-api
```

La base de datos SQLite se persistirá en el volumen montado.

## 🧪 Tests

```bash
# Ejecutar tests unitarios
./mvnw test

# Ejecutar tests con reporte de cobertura
./mvnw test jacoco:report

# Ver reporte de cobertura
open target/site/jacoco/index.html
```

## 📊 Base de Datos

Este backend utiliza **SQLite** como base de datos embebida, ideal para:

- ✅ **Portabilidad**: BD en un solo archivo, fácil de mover
- ✅ **Cero configuración**: No requiere servidor de BD separado
- ✅ **Desarrollo ágil**: Perfecto para prototipos y demos
- ✅ **Fácil dockerización**: Todo incluido en un contenedor

El archivo de BD se crea automáticamente en:
- **Local**: `./users.db` (raíz del backend)
- **Docker**: `/app/data/users.db` (volumen persistente)

### Migrar a PostgreSQL (futuro)

Cambiar solo requiere:
1. Actualizar `pom.xml`: dependencia PostgreSQL en lugar de SQLite
2. Actualizar `application.yml`: URL, usuario, contraseña
3. Cambiar `database-platform` a PostgreSQL dialect

El código de dominio y aplicación **no cambia** (beneficio de Clean Architecture).

## ⚙️ Configuración

### Variables de Entorno

| Variable              | Descripción                | Default            |
|-----------------------|----------------------------|--------------------|
| `SPRING_PROFILES_ACTIVE` | Profile activo (dev/prod) | `dev`              |
| `SERVER_PORT`         | Puerto del servidor        | `8080`             |
| `SQLITE_DB_PATH`      | Ruta archivo SQLite        | `./users.db`       |

### Profiles

- **dev**: Logs detallados, SQL visible, hot reload
- **prod**: Logs mínimos, SQL oculto, optimizado

```bash
# Ejecutar en modo producción
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📝 Validaciones

El backend implementa validaciones exhaustivas:

- **Email único** (case-insensitive)
- **Teléfono colombiano**: formato `+57 3XX XXX XXXX`
- **Fecha de nacimiento**: debe ser pasada, máximo 150 años
- **Nombres/Apellidos**: mínimo 2 caracteres
- **Campos obligatorios**: todos excepto en actualizaciones parciales

Mensajes de error en **español** para coherencia con el frontend.

## 🔍 OpenAPI / Swagger

Documentación interactiva generada automáticamente:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

Permite:
- ✅ Probar endpoints directamente desde el navegador
- ✅ Ver schemas de DTOs
- ✅ Entender contratos de API
- ✅ Generar clientes automáticamente

## 🌐 CORS

Configurado para aceptar requests desde:
- `http://localhost:4200` (Angular dev server)
- `http://localhost` (Docker frontend)
- `http://localhost:80`

Métodos permitidos: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`

## 📂 Estructura de DTOs

### CreateUserDto (entrada)
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "telefono": "+57 300 123 4567",
  "fechaNacimiento": "1990-05-15"
}
```

### UserResponseDto (salida)
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@example.com",
  "telefono": "+57 300 123 4567",
  "fechaNacimiento": "1990-05-15"
}
```

### ErrorResponse (errores)
```json
{
  "error": "Bad Request",
  "message": "Error de validación en los datos enviados",
  "path": "/api/users",
  "timestamp": "2026-03-03T10:30:00",
  "validationErrors": {
    "email": "El email ya está registrado en el sistema"
  }
}
```

## 🎯 Decisiones Técnicas

### ¿Por qué Clean Architecture?

- Separación clara entre lógica de negocio (dominio) e infraestructura
- Dominio testeable sin frameworks
- Facilita evolución y mantenimiento a largo plazo

### ¿Por qué SQLite?

- Simplicidad y portabilidad para MVP/demos
- Cero dependencias externas
- Migración trivial a PostgreSQL cuando escale

### ¿Por qué Spring Boot 3?

- Estándar de la industria para APIs Java
- Ecosistema maduro (Spring Data, Validation, Actuator)
- Excelente integración con OpenAPI/Swagger

## 📄 Licencia

Este proyecto es parte de un monorepo full-stack. Ver LICENSE en la raíz.

## 👤 Autor

Desarrollado como parte del proyecto CRUD Users Full-Stack.
