package com.crud.users.infrastructure.persistence.repository;

import com.crud.users.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para UserEntity.
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    
    /**
     * Busca un usuario por email (case-insensitive).
     */
    Optional<UserEntity> findByEmailIgnoreCase(String email);
    
    /**
     * Verifica si existe un usuario con el email dado (case-insensitive).
     */
    boolean existsByEmailIgnoreCase(String email);
    
    /**
     * Busca usuarios que coincidan con el término de búsqueda en nombre, apellido o email.
     */
    @Query("SELECT u FROM UserEntity u WHERE " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserEntity> searchByTerm(@Param("searchTerm") String searchTerm);
}
