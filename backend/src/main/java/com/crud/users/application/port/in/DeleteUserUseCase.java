package com.crud.users.application.port.in;

/**
 * Puerto de entrada (input port) para eliminar un usuario.
 */
public interface DeleteUserUseCase {

    /**
     * Elimina un usuario del sistema por su identificador.
     *
     * @param id identificador del usuario a eliminar
     */
    void execute(String id);
}
