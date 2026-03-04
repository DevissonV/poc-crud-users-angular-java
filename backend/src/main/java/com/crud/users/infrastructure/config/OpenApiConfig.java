package com.crud.users.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 */
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API CRUD de Usuarios")
                        .version("1.0.0")
                        .description("API REST para gestión de usuarios con arquitectura limpia. " +
                                     "Permite operaciones CRUD completas con validaciones de negocio.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("dev@example.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("http://localhost")
                                .description("Servidor Docker")
                ));
    }
}
