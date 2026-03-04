package com.crud.users.infrastructure.persistence.mapper;

import com.crud.users.domain.model.User;
import com.crud.users.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests unitarios para UserEntityMapper.
 * Verifica la conversión bidireccional entre User (dominio) y UserEntity (JPA).
 */
@DisplayName("UserEntityMapper Tests")
class UserEntityMapperTest {

    private UserEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserEntityMapper();
    }

    // ==================== HAPPY PATHS ====================

    @Test
    @DisplayName("toDomain - convierte UserEntity a User correctamente")
    void toDomain_shouldConvertUserEntityToUser() {
        // Given
        UserEntity entity = new UserEntity(
                "123",
                "Juan",
                "Pérez",
                "juan@example.com",
                "+57 300 123 4567",
                LocalDate.of(1990, 1, 1)
        );

        // When
        User user = mapper.toDomain(entity);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("123");
        assertThat(user.getNombre()).isEqualTo("Juan");
        assertThat(user.getApellido()).isEqualTo("Pérez");
        assertThat(user.getEmail()).isEqualTo("juan@example.com");
        assertThat(user.getTelefono()).isEqualTo("+57 300 123 4567");
        assertThat(user.getFechaNacimiento()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    @DisplayName("toEntity - convierte User a UserEntity correctamente")
    void toEntity_shouldConvertUserToUserEntity() {
        // Given
        User user = new User(
                "456",
                "María",
                "García",
                "maria@example.com",
                "+57 310 456 7890",
                LocalDate.of(1995, 5, 15)
        );

        // When
        UserEntity entity = mapper.toEntity(user);

        // Then
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("456");
        assertThat(entity.getNombre()).isEqualTo("María");
        assertThat(entity.getApellido()).isEqualTo("García");
        assertThat(entity.getEmail()).isEqualTo("maria@example.com");
        assertThat(entity.getTelefono()).isEqualTo("+57 310 456 7890");
        assertThat(entity.getFechaNacimiento()).isEqualTo(LocalDate.of(1995, 5, 15));
    }

    // ==================== SAD PATHS ====================

    @Test
    @DisplayName("toDomain - retorna null cuando entity es null")
    void toDomain_shouldReturnNullWhenEntityIsNull() {
        // When
        User user = mapper.toDomain(null);

        // Then
        assertThat(user).isNull();
    }

    @Test
    @DisplayName("toEntity - retorna null cuando user es null")
    void toEntity_shouldReturnNullWhenUserIsNull() {
        // When
        UserEntity entity = mapper.toEntity(null);

        // Then
        assertThat(entity).isNull();
    }
}
