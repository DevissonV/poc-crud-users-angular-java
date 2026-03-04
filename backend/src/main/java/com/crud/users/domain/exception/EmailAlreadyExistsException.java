package com.crud.users.domain.exception;

/**
 * Excepción lanzada cuando se intenta registrar un usuario con un email que ya existe.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super("El email ya está registrado en el sistema: " + email);
    }
    
    public EmailAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
