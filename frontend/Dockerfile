# Stage 1: Build Angular application
FROM node:20-alpine AS build
WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci --legacy-peer-deps

# Copiar código fuente
COPY . .

# Build para producción
RUN npm run build -- --configuration production

# Stage 2: Servir con Nginx
FROM nginx:1.25-alpine

# Copiar archivos de build
COPY --from=build /app/dist/crud-users-angular/browser /usr/share/nginx/html

# Copiar configuración de nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto 80
EXPOSE 80

# Comando para iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
