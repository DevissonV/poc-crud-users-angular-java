package com.crud.users.domain.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario.
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String id) {
        super("Usuario no encontrado con ID: " + id);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
