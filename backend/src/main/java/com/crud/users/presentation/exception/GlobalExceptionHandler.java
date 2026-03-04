package com.crud.users.presentation.exception;

import com.crud.users.domain.exception.EmailAlreadyExistsException;
import com.crud.users.domain.exception.UserNotFoundException;
import com.crud.users.presentation.response.ApiResponse;
import com.crud.users.presentation.response.ErrorData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Maneja excepciones de usuario no encontrado.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleUserNotFoundException(
            UserNotFoundException ex, WebRequest request) {
        
        String path = request.getDescription(false).replace("uri=", "");
        ErrorData errorData = new ErrorData("Not Found", path);
        ApiResponse<ErrorData> response = ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        response.setData(errorData);
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    /**
     * Maneja excepciones de email duplicado.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException ex, WebRequest request) {
        
        String path = request.getDescription(false).replace("uri=", "");
        ErrorData errorData = new ErrorData("Conflict", path);
        ApiResponse<ErrorData> response = ApiResponse.error(ex.getMessage(), HttpStatus.CONFLICT.value());
        response.setData(errorData);
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * Maneja errores de validación de Bean Validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> validationErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });
        
        String path = request.getDescription(false).replace("uri=", "");
        ErrorData errorData = new ErrorData("Bad Request", path, validationErrors);
        ApiResponse<ErrorData> response = ApiResponse.error(
                "Error de validación en los datos enviados", 
                HttpStatus.BAD_REQUEST.value()
        );
        response.setData(errorData);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Maneja excepciones de argumentos ilegales.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        String path = request.getDescription(false).replace("uri=", "");
        ErrorData errorData = new ErrorData("Bad Request", path);
        ApiResponse<ErrorData> response = ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
        response.setData(errorData);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * Maneja cualquier otra excepción no contemplada.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorData>> handleGlobalException(
            Exception ex, WebRequest request) {
        
        String path = request.getDescription(false).replace("uri=", "");
        ErrorData errorData = new ErrorData("Internal Server Error", path);
        ApiResponse<ErrorData> response = ApiResponse.error(
                "Ha ocurrido un error inesperado. Por favor, contacte al administrador.", 
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        response.setData(errorData);
        
        // En producción, no exponer detalles internos
        // Log the exception for debugging
        ex.printStackTrace();
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
