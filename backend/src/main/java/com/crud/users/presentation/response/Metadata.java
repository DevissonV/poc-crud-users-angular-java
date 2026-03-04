package com.crud.users.presentation.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

/**
 * Metadata para las respuestas de la API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Metadata {
    
    private LocalDateTime timestamp;
    private Integer status;
    private String message;
    private Long totalItems;

    public Metadata() {
        this.timestamp = LocalDateTime.now();
    }

    public Metadata(Integer status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public Metadata(Integer status, String message, Long totalItems) {
        this(status, message);
        this.totalItems = totalItems;
    }

    // Getters y Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }
}
