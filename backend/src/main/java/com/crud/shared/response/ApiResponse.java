package com.crud.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Respuesta genérica de la API con estructura unificada.
 * 
 * @param <T> Tipo de datos de la respuesta
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private T data;
    private Metadata metadata;

    public ApiResponse() {
    }

    public ApiResponse(T data, Metadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    // Métodos helper para crear respuestas comunes
    public static <T> ApiResponse<T> success(T data, String message) {
        Metadata metadata = new Metadata(200, message);
        return new ApiResponse<>(data, metadata);
    }

    public static <T> ApiResponse<T> success(T data, Integer status, String message) {
        Metadata metadata = new Metadata(status, message);
        return new ApiResponse<>(data, metadata);
    }

    public static <T> ApiResponse<T> successList(T data, String message, Long totalItems) {
        Metadata metadata = new Metadata(200, message, totalItems);
        return new ApiResponse<>(data, metadata);
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        Metadata metadata = new Metadata(201, message);
        return new ApiResponse<>(data, metadata);
    }

    public static <T> ApiResponse<T> noContent(String message) {
        Metadata metadata = new Metadata(200, message);
        return new ApiResponse<>(null, metadata);
    }

    public static <T> ApiResponse<T> error(String message, Integer status) {
        Metadata metadata = new Metadata(status, message);
        return new ApiResponse<>(null, metadata);
    }

    // Getters y Setters
    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
