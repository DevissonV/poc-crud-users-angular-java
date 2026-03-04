package com.crud.users.application.port.in;

import com.crud.users.application.dto.UserResponseDto;

/**
 * Puerto de entrada (input port) para obtener un usuario por ID.
 */
public interface GetUserByIdUseCase {

    /**
     * Busca y retorna un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return usuario encontrado como DTO de respuesta
     */
    UserResponseDto execute(String id);
}
