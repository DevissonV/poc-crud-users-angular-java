package com.crud.users.presentation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal de la aplicación Spring Boot.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.crud.users")
@EntityScan(basePackages = "com.crud.users.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "com.crud.users.infrastructure.persistence.repository")
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}
