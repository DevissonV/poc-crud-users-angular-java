package com.crud.users.application.port.in;

import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;

/**
 * Puerto de entrada (input port) para actualizar un usuario existente.
 */
public interface UpdateUserUseCase {

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param id            identificador del usuario a actualizar
     * @param updateUserDto datos a actualizar
     * @return usuario actualizado como DTO de respuesta
     */
    UserResponseDto execute(String id, UpdateUserDto updateUserDto);
}
