package com.crud.users;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.UserApplication;
import com.crud.shared.response.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests de integración end-to-end para todo el stack de la aplicación.
 * 
 * Prueba el flujo completo: Controller -> Service -> Repository -> Database
 */
@SpringBootTest(
    classes = UserApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Integration Tests - E2E")
class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private static String createdUserId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/users";
    }

    @Test
    @Order(1)
    @DisplayName("Should create user successfully - E2E")
    void shouldCreateUserEndToEnd() throws Exception {
        // Given
        CreateUserDto createDto = new CreateUserDto();
        createDto.setNombre("Integration");
        createDto.setApellido("Test");
        createDto.setEmail("integration@test.com");
        createDto.setTelefono("+57 300 111 2222");
        createDto.setFechaNacimiento(LocalDate.of(1990, 6, 15));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                createDto,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getHeaders().getLocation()).isNotNull();
        
        // Parse ApiResponse
        ApiResponse<UserResponseDto> apiResponse = objectMapper.readValue(
            response.getBody(), 
            new TypeReference<ApiResponse<UserResponseDto>>() {}
        );
        
        assertThat(apiResponse.getData()).isNotNull();
        assertThat(apiResponse.getData().getNombre()).isEqualTo("Integration");
        assertThat(apiResponse.getData().getEmail()).isEqualTo("integration@test.com");
        assertThat(apiResponse.getData().getId()).isNotNull();
        assertThat(apiResponse.getMetadata().getStatus()).isEqualTo(201);

        // Save ID for subsequent tests
        createdUserId = apiResponse.getData().getId();
    }

    @Test
    @Order(2)
    @DisplayName("Should get all users - E2E")
    void shouldGetAllUsersEndToEnd() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        ApiResponse<List<UserResponseDto>> apiResponse = objectMapper.readValue(
            response.getBody(), 
            new TypeReference<ApiResponse<List<UserResponseDto>>>() {}
        );
        
        assertThat(apiResponse.getData()).isNotNull();
        assertThat(apiResponse.getData().size()).isGreaterThanOrEqualTo(1);
        assertThat(apiResponse.getMetadata().getStatus()).isEqualTo(200);
        assertThat(apiResponse.getMetadata().getTotalItems()).isEqualTo(apiResponse.getData().size());
    }

    @Test
    @Order(3)
    @DisplayName("Should get user by id - E2E")
    void shouldGetUserByIdEndToEnd() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/" + createdUserId,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        ApiResponse<UserResponseDto> apiResponse = objectMapper.readValue(
            response.getBody(), 
            new TypeReference<ApiResponse<UserResponseDto>>() {}
        );
        
        assertThat(apiResponse.getData()).isNotNull();
        assertThat(apiResponse.getData().getId()).isEqualTo(createdUserId);
        assertThat(apiResponse.getData().getEmail()).isEqualTo("integration@test.com");
        assertThat(apiResponse.getMetadata().getStatus()).isEqualTo(200);
    }

    @Test
    @Order(4)
    @DisplayName("Should search users - E2E")
    void shouldSearchUsersEndToEnd() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/search?q=Integration",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        ApiResponse<List<UserResponseDto>> apiResponse = objectMapper.readValue(
            response.getBody(), 
            new TypeReference<ApiResponse<List<UserResponseDto>>>() {}
        );
        
        assertThat(apiResponse.getData()).isNotNull();
        assertThat(apiResponse.getData().size()).isGreaterThanOrEqualTo(1);
        assertThat(apiResponse.getData().get(0).getNombre()).contains("Integration");
    }

    @Test
    @Order(5)
    @DisplayName("Should update user - E2E")
    void shouldUpdateUserEndToEnd() throws Exception {
        // Given
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(createdUserId);
        updateDto.setNombre("Updated Integration");
        updateDto.setApellido("Updated Test");

        HttpEntity<UpdateUserDto> request = new HttpEntity<>(updateDto);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + createdUserId,
                HttpMethod.PUT,
                request,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        ApiResponse<UserResponseDto> apiResponse = objectMapper.readValue(
            response.getBody(), 
            new TypeReference<ApiResponse<UserResponseDto>>() {}
        );
        
        assertThat(apiResponse.getData()).isNotNull();
        assertThat(apiResponse.getData().getNombre()).isEqualTo("Updated Integration");
        assertThat(apiResponse.getData().getApellido()).isEqualTo("Updated Test");
        assertThat(apiResponse.getData().getEmail()).isEqualTo("integration@test.com"); // Unchanged
    }

    @Test
    @Order(6)
    @DisplayName("Should return 409 when creating user with existing email - E2E")
    void shouldReturn409ForDuplicateEmailEndToEnd() {
        // Given
        CreateUserDto duplicateDto = new CreateUserDto();
        duplicateDto.setNombre("Duplicate");
        duplicateDto.setApellido("User");
        duplicateDto.setEmail("integration@test.com"); // Email already exists
        duplicateDto.setTelefono("+57 310 222 3333");
        duplicateDto.setFechaNacimiento(LocalDate.of(1995, 3, 10));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                duplicateDto,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @Order(7)
    @DisplayName("Should return 400 for invalid data - E2E")
    void shouldReturn400ForInvalidDataEndToEnd() {
        // Given
        CreateUserDto invalidDto = new CreateUserDto();
        invalidDto.setNombre(""); // Invalid - required
        invalidDto.setEmail("invalid-email"); // Invalid format
        invalidDto.setTelefono("123"); // Invalid phone
        invalidDto.setFechaNacimiento(LocalDate.of(1800, 1, 1)); // Too old

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                invalidDto,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Order(8)
    @DisplayName("Should return 404 when getting non-existent user - E2E")
    void shouldReturn404ForNonExistentUserEndToEnd() {
        // When
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/nonexistent-id-999",
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(9)
    @DisplayName("Should delete user - E2E")
    void shouldDeleteUserEndToEnd() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/" + createdUserId,
                HttpMethod.DELETE,
                null,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        
        ApiResponse<Void> apiResponse = objectMapper.readValue(
            response.getBody(), 
            new TypeReference<ApiResponse<Void>>() {}
        );
        
        assertThat(apiResponse.getMetadata().getStatus()).isEqualTo(200);
        assertThat(apiResponse.getMetadata().getMessage()).contains("eliminado");

        // Verify deletion
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + createdUserId,
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(10)
    @DisplayName("Should validate Colombian phone format - E2E")
    void shouldValidateColombianPhoneEndToEnd() throws Exception {
        // Given - Valid phone formats
        CreateUserDto validPhoneDto = new CreateUserDto();
        validPhoneDto.setNombre("Phone");
        validPhoneDto.setApellido("Test");
        validPhoneDto.setEmail("phone@test.com");
        validPhoneDto.setTelefono("+57 315 444 5555");
        validPhoneDto.setFechaNacimiento(LocalDate.of(1992, 8, 20));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                validPhoneDto,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        
        ApiResponse<UserResponseDto> apiResponse = objectMapper.readValue(
            response.getBody(), 
            new TypeReference<ApiResponse<UserResponseDto>>() {}
        );

        // Cleanup
        if (apiResponse.getData() != null) {
            restTemplate.delete(baseUrl + "/" + apiResponse.getData().getId());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Should reject age over 150 years - E2E")
    void shouldRejectAgeOver150EndToEnd() {
        // Given
        CreateUserDto tooOldDto = new CreateUserDto();
        tooOldDto.setNombre("Ancient");
        tooOldDto.setApellido("User");
        tooOldDto.setEmail("ancient@test.com");
        tooOldDto.setTelefono("+57 300 999 8888");
        tooOldDto.setFechaNacimiento(LocalDate.of(1800, 1, 1));

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl,
                tooOldDto,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("150");
    }

    @Test
    @Order(12)
    @DisplayName("Should handle complete user lifecycle - E2E")
    void shouldHandleCompleteUserLifecycleEndToEnd() throws Exception {
        // 1. Create
        CreateUserDto createDto = new CreateUserDto();
        createDto.setNombre("Lifecycle");
        createDto.setApellido("User");
        createDto.setEmail("lifecycle." + System.currentTimeMillis() + "@test.com");
        createDto.setTelefono("+57 320 555 6666");
        createDto.setFechaNacimiento(LocalDate.of(1987, 12, 5));

        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                baseUrl,
                createDto,
                String.class
        );
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        ApiResponse<UserResponseDto> createApiResponse = objectMapper.readValue(
            createResponse.getBody(), 
            new TypeReference<ApiResponse<UserResponseDto>>() {}
        );
        String userId = createApiResponse.getData().getId();

        // 2. Read
        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                baseUrl + "/" + userId,
                String.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ApiResponse<UserResponseDto> getApiResponse = objectMapper.readValue(
            getResponse.getBody(), 
            new TypeReference<ApiResponse<UserResponseDto>>() {}
        );
        assertThat(getApiResponse.getData().getNombre()).isEqualTo("Lifecycle");

        // 3. Update
        UpdateUserDto updateDto = new UpdateUserDto();
        updateDto.setId(userId);
        updateDto.setNombre("Lifecycle Updated");
        
        ResponseEntity<String> updateResponse = restTemplate.exchange(
                baseUrl + "/" + userId,
                HttpMethod.PUT,
                new HttpEntity<>(updateDto),
                String.class
        );
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        ApiResponse<UserResponseDto> updateApiResponse = objectMapper.readValue(
            updateResponse.getBody(), 
            new TypeReference<ApiResponse<UserResponseDto>>() {}
        );
        assertThat(updateApiResponse.getData().getNombre()).isEqualTo("Lifecycle Updated");

        // 4. Delete
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
                baseUrl + "/" + userId,
                HttpMethod.DELETE,
                null,
                String.class
        );
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // 5. Verify deleted
        ResponseEntity<String> verifyResponse = restTemplate.getForEntity(
                baseUrl + "/" + userId,
                String.class
        );
        assertThat(verifyResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
