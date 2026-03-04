package com.crud.users.application.port.in;

import com.crud.users.application.dto.UserResponseDto;

import java.util.List;

/**
 * Puerto de entrada (input port) para buscar usuarios.
 */
public interface SearchUsersUseCase {

    /**
     * Busca usuarios que coincidan con el término de búsqueda en nombre, apellido o email.
     *
     * @param searchTerm término de búsqueda
     * @return lista de usuarios que coinciden como DTOs de respuesta
     */
    List<UserResponseDto> execute(String searchTerm);
}
