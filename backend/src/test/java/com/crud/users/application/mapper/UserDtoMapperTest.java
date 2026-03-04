package com.crud.users.application.mapper;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Suite de pruebas para UserDtoMapper
 * 
 * Verifica la correcta conversión entre entidades de dominio y DTOs.
 */
@DisplayName("UserDtoMapper - Unit Tests")
class UserDtoMapperTest {

    private UserDtoMapper mapper;
    private User user;
    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        mapper = new UserDtoMapper();

        user = new User(
                "1",
                "Juan",
                "Pérez",
                "juan@example.com",
                "+57 300 123 4567",
                LocalDate.of(1990, 1, 15)
        );

        createUserDto = new CreateUserDto();
        createUserDto.setNombre("Pedro");
        createUserDto.setApellido("López");
        createUserDto.setEmail("pedro@example.com");
        createUserDto.setTelefono("+57 320 789 1234");
        createUserDto.setFechaNacimiento(LocalDate.of(1988, 3, 20));

        updateUserDto = new UpdateUserDto();
        updateUserDto.setNombre("Juan Actualizado");
        updateUserDto.setEmail("juan.nuevo@example.com");
    }

    @Test
    @DisplayName("Should map User to UserResponseDto successfully")
    void shouldMapUserToResponseDto() {
        // When
        UserResponseDto result = mapper.toResponseDto(user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getNombre()).isEqualTo("Juan");
        assertThat(result.getApellido()).isEqualTo("Pérez");
        assertThat(result.getEmail()).isEqualTo("juan@example.com");
        assertThat(result.getTelefono()).isEqualTo("+57 300 123 4567");
        assertThat(result.getFechaNacimiento()).isEqualTo("1990-01-15");
    }

    @Test
    @DisplayName("Should return null when mapping null User to ResponseDto")
    void shouldReturnNullWhenMappingNullUser() {
        // When
        UserResponseDto result = mapper.toResponseDto(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should handle null fecha nacimiento when mapping to ResponseDto")
    void shouldHandleNullFechaNacimientoInResponseDto() {
        // Given
        user.setFechaNacimiento(null);

        // When
        UserResponseDto result = mapper.toResponseDto(user);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFechaNacimiento()).isNull();
    }

    @Test
    @DisplayName("Should map CreateUserDto to User successfully")
    void shouldMapCreateUserDtoToDomain() {
        // When
        User result = mapper.toDomain(createUserDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull(); // UUID generated
        assertThat(result.getNombre()).isEqualTo("Pedro");
        assertThat(result.getApellido()).isEqualTo("López");
        assertThat(result.getEmail()).isEqualTo("pedro@example.com");
        assertThat(result.getTelefono()).isEqualTo("+57 320 789 1234");
        assertThat(result.getFechaNacimiento()).isEqualTo(LocalDate.of(1988, 3, 20));
    }

    @Test
    @DisplayName("Should generate UUID when mapping CreateUserDto to User")
    void shouldGenerateUuidWhenMappingToDomain() {
        // When
        User result1 = mapper.toDomain(createUserDto);
        User result2 = mapper.toDomain(createUserDto);

        // Then
        assertThat(result1.getId()).isNotNull();
        assertThat(result2.getId()).isNotNull();
        assertThat(result1.getId()).isNotEqualTo(result2.getId());
    }

    @Test
    @DisplayName("Should return null when mapping null CreateUserDto")
    void shouldReturnNullWhenMappingNullCreateDto() {
        // When
        User result = mapper.toDomain(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should update User with UpdateUserDto successfully")
    void shouldUpdateDomainWithUpdateDto() {
        // Given
        String originalEmail = user.getEmail();
        String originalTelefono = user.getTelefono();

        // When
        mapper.updateDomain(user, updateUserDto);

        // Then
        assertThat(user.getNombre()).isEqualTo("Juan Actualizado");
        assertThat(user.getEmail()).isEqualTo("juan.nuevo@example.com");
        // Fields not in updateDto should remain unchanged
        assertThat(user.getTelefono()).isEqualTo(originalTelefono);
    }

    @Test
    @DisplayName("Should only update non-null fields from UpdateUserDto")
    void shouldOnlyUpdateNonNullFields() {
        // Given
        UpdateUserDto partialUpdate = new UpdateUserDto();
        partialUpdate.setNombre("Nuevo Nombre");
        // Other fields are null

        String originalApellido = user.getApellido();
        String originalEmail = user.getEmail();
        String originalTelefono = user.getTelefono();
        LocalDate originalFecha = user.getFechaNacimiento();

        // When
        mapper.updateDomain(user, partialUpdate);

        // Then
        assertThat(user.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(user.getApellido()).isEqualTo(originalApellido);
        assertThat(user.getEmail()).isEqualTo(originalEmail);
        assertThat(user.getTelefono()).isEqualTo(originalTelefono);
        assertThat(user.getFechaNacimiento()).isEqualTo(originalFecha);
    }

    @Test
    @DisplayName("Should handle null UpdateUserDto")
    void shouldHandleNullUpdateDto() {
        // Given
        User originalUser = new User(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getTelefono(),
                user.getFechaNacimiento()
        );

        // When
        mapper.updateDomain(user, null);

        // Then - User should remain unchanged
        assertThat(user.getNombre()).isEqualTo(originalUser.getNombre());
        assertThat(user.getApellido()).isEqualTo(originalUser.getApellido());
        assertThat(user.getEmail()).isEqualTo(originalUser.getEmail());
    }

    @Test
    @DisplayName("Should handle null User in updateDomain")
    void shouldHandleNullUserInUpdate() {
        // When & Then - Should not throw exception
        mapper.updateDomain(null, updateUserDto);
    }

    @Test
    @DisplayName("Should update all fields when all are provided")
    void shouldUpdateAllFieldsWhenAllProvided() {
        // Given
        UpdateUserDto fullUpdate = new UpdateUserDto();
        fullUpdate.setNombre("Nombre Nuevo");
        fullUpdate.setApellido("Apellido Nuevo");
        fullUpdate.setEmail("nuevo@example.com");
        fullUpdate.setTelefono("+57 310 999 8888");
        fullUpdate.setFechaNacimiento(LocalDate.of(1985, 6, 10));

        // When
        mapper.updateDomain(user, fullUpdate);

        // Then
        assertThat(user.getNombre()).isEqualTo("Nombre Nuevo");
        assertThat(user.getApellido()).isEqualTo("Apellido Nuevo");
        assertThat(user.getEmail()).isEqualTo("nuevo@example.com");
        assertThat(user.getTelefono()).isEqualTo("+57 310 999 8888");
        assertThat(user.getFechaNacimiento()).isEqualTo(LocalDate.of(1985, 6, 10));
    }

    @Test
    @DisplayName("Should correctly format date as string in ResponseDto")
    void shouldFormatDateCorrectlyInResponseDto() {
        // Given
        user.setFechaNacimiento(LocalDate.of(2000, 12, 25));

        // When
        UserResponseDto result = mapper.toResponseDto(user);

        // Then
        assertThat(result.getFechaNacimiento()).isEqualTo("2000-12-25");
    }
}
