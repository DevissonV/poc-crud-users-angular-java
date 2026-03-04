package com.crud.users.application.port.in;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UserResponseDto;

/**
 * Puerto de entrada (input port) para crear un nuevo usuario.
 */
public interface CreateUserUseCase {

    /**
     * Crea un nuevo usuario con los datos proporcionados.
     *
     * @param createUserDto datos del usuario a crear
     * @return usuario creado como DTO de respuesta
     */
    UserResponseDto execute(CreateUserDto createUserDto);
}
