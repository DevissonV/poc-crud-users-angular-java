package com.crud.users.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

/**
 * Datos de error para respuestas de API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorData {
    
    private String error;
    private String path;
    private Map<String, String> validationErrors;

    public ErrorData() {
    }

    public ErrorData(String error, String path) {
        this.error = error;
        this.path = path;
    }

    public ErrorData(String error, String path, Map<String, String> validationErrors) {
        this.error = error;
        this.path = path;
        this.validationErrors = validationErrors;
    }

    // Getters y Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
