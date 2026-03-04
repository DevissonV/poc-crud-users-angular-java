package com.crud.users.application.port.out;

import com.crud.users.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (output port) para la persistencia de usuarios.
 * Define el contrato que la capa de aplicación impone a la infraestructura.
 * Implementado por el adaptador UserRepositoryAdapter en la capa de infraestructura.
 */
public interface UserRepositoryPort {

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de usuarios
     */
    List<User> findAll();

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findById(String id);

    /**
     * Busca un usuario por su email (case-insensitive).
     *
     * @param email email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Guarda un nuevo usuario o actualiza uno existente.
     *
     * @param user usuario a guardar
     * @return usuario guardado
     */
    User save(User user);

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario
     */
    void deleteById(String id);

    /**
     * Verifica si existe un usuario con el email dado (case-insensitive).
     *
     * @param email email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios que coincidan con el término de búsqueda en nombre, apellido o email.
     *
     * @param searchTerm término de búsqueda
     * @return lista de usuarios que coinciden
     */
    List<User> searchByTerm(String searchTerm);
}
