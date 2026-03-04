package com.crud.users.application.port.in;

import com.crud.users.application.dto.UserResponseDto;

import java.util.List;

/**
 * Puerto de entrada (input port) para obtener todos los usuarios.
 */
public interface GetAllUsersUseCase {

    /**
     * Retorna la lista completa de usuarios registrados.
     *
     * @return lista de usuarios como DTOs de respuesta
     */
    List<UserResponseDto> execute();
}
