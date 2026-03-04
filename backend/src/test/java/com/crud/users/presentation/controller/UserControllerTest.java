package com.crud.users.presentation.controller;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.port.in.CreateUserUseCase;
import com.crud.users.application.port.in.DeleteUserUseCase;
import com.crud.users.application.port.in.GetAllUsersUseCase;
import com.crud.users.application.port.in.GetUserByIdUseCase;
import com.crud.users.application.port.in.SearchUsersUseCase;
import com.crud.users.application.port.in.UpdateUserUseCase;
import com.crud.users.domain.exception.EmailAlreadyExistsException;
import com.crud.users.domain.exception.UserNotFoundException;
import com.crud.users.presentation.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Suite de pruebas para UserController (standalone, sin contexto Spring completo).
 *
 * Verifica el correcto funcionamiento de los endpoints REST,
 * incluyendo validaciones y manejo de errores HTTP.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController - Unit Tests")
class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock private GetAllUsersUseCase getAllUsersUseCase;
    @Mock private GetUserByIdUseCase getUserByIdUseCase;
    @Mock private CreateUserUseCase createUserUseCase;
    @Mock private UpdateUserUseCase updateUserUseCase;
    @Mock private DeleteUserUseCase deleteUserUseCase;
    @Mock private SearchUsersUseCase searchUsersUseCase;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    @DisplayName("GET /api/users - Should return all users")
    void shouldGetAllUsers() throws Exception {
        // Given
        List<UserResponseDto> users = Arrays.asList(
                new UserResponseDto("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", "1990-01-15"),
                new UserResponseDto("2", "María", "García", "maria@example.com", "+57 310 456 7890", "1995-05-20")
        );
        given(getAllUsersUseCase.execute()).willReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].nombre", is("Juan")))
                .andExpect(jsonPath("$.data[1].nombre", is("María")))
                .andExpect(jsonPath("$.metadata.status", is(200)))
                .andExpect(jsonPath("$.metadata.totalItems", is(2)));
    }

    @Test
    @DisplayName("GET /api/users - Should return empty list when no users")
    void shouldReturnEmptyListWhenNoUsers() throws Exception {
        // Given
        given(getAllUsersUseCase.execute()).willReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.metadata.totalItems", is(0)));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return user by id")
    void shouldGetUserById() throws Exception {
        // Given
        UserResponseDto user = new UserResponseDto("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", "1990-01-15");
        given(getUserByIdUseCase.execute("1")).willReturn(user);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is("1")))
                .andExpect(jsonPath("$.data.nombre", is("Juan")))
                .andExpect(jsonPath("$.data.email", is("juan@example.com")))
                .andExpect(jsonPath("$.metadata.status", is(200)));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Should return 404 when user not found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Given
        given(getUserByIdUseCase.execute("999")).willThrow(new UserNotFoundException("999"));

        // When & Then
        mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/search - Should search users by term")
    void shouldSearchUsers() throws Exception {
        // Given
        List<UserResponseDto> users = Arrays.asList(
                new UserResponseDto("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", "1990-01-15")
        );
        given(searchUsersUseCase.execute("Juan")).willReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                        .param("q", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].nombre", is("Juan")))
                .andExpect(jsonPath("$.metadata.totalItems", is(1)));
    }

    @Test
    @DisplayName("POST /api/users - Should create user successfully")
    void shouldCreateUser() throws Exception {
        // Given
        CreateUserDto createDto = new CreateUserDto();
        createDto.setNombre("Pedro");
        createDto.setApellido("López");
        createDto.setEmail("pedro@example.com");
        createDto.setTelefono("+57 320 789 1234");
        createDto.setFechaNacimiento(LocalDate.of(1988, 3, 20));

        UserResponseDto createdUser = new UserResponseDto("3", "Pedro", "López", "pedro@example.com", "+57 320 789 1234", "1988-03-20");
        given(createUserUseCase.execute(any(CreateUserDto.class))).willReturn(createdUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.data.nombre", is("Pedro")))
                .andExpect(jsonPath("$.data.email", is("pedro@example.com")))
                .andExpect(jsonPath("$.metadata.status", is(201)));
    }

    @Test
    @DisplayName("POST /api/users - Should return 400 when validation fails")
    void shouldReturn400WhenValidationFails() throws Exception {
        // Given
        CreateUserDto invalidDto = new CreateUserDto();
        invalidDto.setNombre(""); // Invalid - required
        invalidDto.setEmail("invalid-email"); // Invalid format

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Should return 409 when email already exists")
    void shouldReturn409WhenEmailExists() throws Exception {
        // Given
        CreateUserDto createDto = new CreateUserDto();
        createDto.setNombre("Pedro");
        createDto.setApellido("López");
        createDto.setEmail("existing@example.com");
        createDto.setTelefono("+57 320 789 1234");
        createDto.setFechaNacimiento(LocalDate.of(1988, 3, 20));

        given(createUserUseCase.execute(any(CreateUserDto.class)))
                .willThrow(new EmailAlreadyExistsException("existing@example.com"));

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should update user successfully")
    void shouldUpdateUser() throws Exception {
        // Given
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId("1");
        updateDto.setNombre("Juan Actualizado");
        updateDto.setEmail("juan.nuevo@example.com");

        UserResponseDto updatedUser = new UserResponseDto("1", "Juan Actualizado", "Pérez", "juan.nuevo@example.com", "+57 300 123 4567", "1990-01-15");
        given(updateUserUseCase.execute(eq("1"), any(UpdateUserDto.class))).willReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre", is("Juan Actualizado")))
                .andExpect(jsonPath("$.data.email", is("juan.nuevo@example.com")))
                .andExpect(jsonPath("$.metadata.status", is(200)));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should return 404 when updating non-existent user")
    void shouldReturn404WhenUpdatingNonExistentUser() throws Exception {
        // Given
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId("999");
        updateDto.setNombre("Test");

        given(updateUserUseCase.execute(eq("999"), any(UpdateUserDto.class)))
                .willThrow(new UserNotFoundException("999"));

        // When & Then
        mockMvc.perform(put("/api/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Should return 409 when updating with existing email")
    void shouldReturn409WhenUpdatingWithExistingEmail() throws Exception {
        // Given
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId("1");
        updateDto.setEmail("existing@example.com");

        given(updateUserUseCase.execute(eq("1"), any(UpdateUserDto.class)))
                .willThrow(new EmailAlreadyExistsException("existing@example.com"));

        // When & Then
        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should delete user successfully")
    void shouldDeleteUser() throws Exception {
        // Given
        doNothing().when(deleteUserUseCase).execute("1");

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.metadata.status", is(200)))
                .andExpect(jsonPath("$.metadata.message").exists());

        verify(deleteUserUseCase).execute("1");
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Should return 404 when deleting non-existent user")
    void shouldReturn404WhenDeletingNonExistentUser() throws Exception {
        // Given
        doThrow(new UserNotFoundException("999")).when(deleteUserUseCase).execute("999");

        // When & Then
        mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should respond with CORS header for allowed origin")
    void shouldHandleCorsHeaders() throws Exception {
        given(getAllUsersUseCase.execute()).willReturn(Arrays.asList());

        // Regular GET with Origin header — @CrossOrigin on controller adds Allow-Origin header
        mockMvc.perform(get("/api/users")
                        .header("Origin", "http://localhost:4200"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
}
