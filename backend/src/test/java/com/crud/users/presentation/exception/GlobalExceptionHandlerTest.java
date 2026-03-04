package com.crud.users.presentation.exception;

import com.crud.users.domain.exception.EmailAlreadyExistsException;
import com.crud.users.domain.exception.UserNotFoundException;
import com.crud.users.presentation.response.ApiResponse;
import com.crud.users.presentation.response.ErrorData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * Suite de pruebas para GlobalExceptionHandler
 * 
 * Verifica el correcto manejo de excepciones y la generación
 * de respuestas HTTP apropiadas.
 */
@DisplayName("GlobalExceptionHandler - Unit Tests")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final WebRequest mockRequest = mock(WebRequest.class);

    @Test
    @DisplayName("Should handle UserNotFoundException with 404 status")
    void shouldHandleUserNotFoundException() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("123");
        given(mockRequest.getDescription(false)).willReturn("uri=/api/users/123");

        // When
        ResponseEntity<ApiResponse<ErrorData>> response = exceptionHandler.handleUserNotFoundException(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMetadata()).isNotNull();
        assertThat(response.getBody().getMetadata().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMetadata().getMessage()).contains("123");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getError()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Should handle EmailAlreadyExistsException with 409 status")
    void shouldHandleEmailAlreadyExistsException() {
        // Given
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("test@example.com");
        given(mockRequest.getDescription(false)).willReturn("uri=/api/users");

        // When
        ResponseEntity<ApiResponse<ErrorData>> response = exceptionHandler.handleEmailAlreadyExistsException(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMetadata()).isNotNull();
        assertThat(response.getBody().getMetadata().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMetadata().getMessage()).contains("test@example.com");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getError()).isEqualTo("Conflict");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with 400 status")
    void shouldHandleMethodArgumentNotValidException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);
        
        FieldError fieldError1 = new FieldError("createUserDto", "nombre", "El nombre es requerido");
        FieldError fieldError2 = new FieldError("createUserDto", "email", "El email no es válido");
        
        given(bindingResult.getAllErrors()).willReturn(Arrays.asList(fieldError1, fieldError2));
        given(mockRequest.getDescription(false)).willReturn("uri=/api/users");

        // When
        ResponseEntity<ApiResponse<ErrorData>> response = exceptionHandler.handleValidationExceptions(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMetadata()).isNotNull();
        assertThat(response.getBody().getMetadata().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMetadata().getMessage()).contains("validación");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getData().getValidationErrors()).isNotNull();
        assertThat(response.getBody().getData().getValidationErrors()).hasSize(2);
        assertThat(response.getBody().getData().getValidationErrors()).containsKey("nombre");
        assertThat(response.getBody().getData().getValidationErrors()).containsKey("email");
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with 400 status")
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException("La fecha de nacimiento no puede ser mayor a 150 años");
        given(mockRequest.getDescription(false)).willReturn("uri=/api/users");

        // When
        ResponseEntity<ApiResponse<ErrorData>> response = exceptionHandler.handleIllegalArgumentException(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMetadata()).isNotNull();
        assertThat(response.getBody().getMetadata().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getMetadata().getMessage()).contains("150 años");
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getError()).isEqualTo("Bad Request");
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");
        given(mockRequest.getDescription(false)).willReturn("uri=/api/users");

        // When
        ResponseEntity<ApiResponse<ErrorData>> response = exceptionHandler.handleGlobalException(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMetadata()).isNotNull();
        assertThat(response.getBody().getMetadata().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getError()).isEqualTo("Internal Server Error");
    }

    @Test
    @DisplayName("Should strip 'uri=' prefix from path")
    void shouldStripUriPrefixFromPath() {
        // Given
        UserNotFoundException exception = new UserNotFoundException("1");
        given(mockRequest.getDescription(false)).willReturn("uri=/api/users/1");

        // When
        ResponseEntity<ApiResponse<ErrorData>> response = exceptionHandler.handleUserNotFoundException(exception, mockRequest);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isNotNull();
        assertThat(response.getBody().getData().getPath()).isEqualTo("/api/users/1");
        assertThat(response.getBody().getData().getPath()).doesNotContain("uri=");
    }
}
