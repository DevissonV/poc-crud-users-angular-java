package com.crud.users.infrastructure.persistence.mapper;

import com.crud.users.domain.model.User;
import com.crud.users.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre UserEntity (JPA) y User (dominio).
 */
@Component
public class UserEntityMapper {
    
    /**
     * Convierte una entidad JPA a entidad de dominio.
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new User(
                entity.getId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getEmail(),
                entity.getTelefono(),
                entity.getFechaNacimiento()
        );
    }
    
    /**
     * Convierte una entidad de dominio a entidad JPA.
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserEntity(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getTelefono(),
                user.getFechaNacimiento()
        );
    }
}
