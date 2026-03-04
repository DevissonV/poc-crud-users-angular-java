# Stage 1: Build con Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build

# Copiar pom.xml y descargar dependencias (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime con OpenJDK
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear directorio para SQLite
RUN mkdir -p /app/data

# Copiar JAR desde stage build
COPY --from=build /build/target/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV SQLITE_DB_PATH=/app/data/users.db

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
