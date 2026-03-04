package com.crud.users.application.mapper;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.domain.model.User;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper para convertir entre entidades de dominio y DTOs.
 */
@Component
public class UserDtoMapper {
    
    /**
     * Convierte un User de dominio a UserResponseDto.
     */
    public UserResponseDto toResponseDto(User user) {
        if (user == null) {
            return null;
        }
        
        return new UserResponseDto(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getEmail(),
                user.getTelefono(),
                user.getFechaNacimiento() != null ? user.getFechaNacimiento().toString() : null
        );
    }
    
    /**
     * Convierte un CreateUserDto a entidad de dominio User.
     * Genera un UUID automáticamente.
     */
    public User toDomain(CreateUserDto dto) {
        if (dto == null) {
            return null;
        }
        
        return new User(
                UUID.randomUUID().toString(),
                dto.getNombre(),
                dto.getApellido(),
                dto.getEmail(),
                dto.getTelefono(),
                dto.getFechaNacimiento()
        );
    }
    
    /**
     * Actualiza un User existente con los datos de UpdateUserDto.
     * Solo actualiza los campos no nulos.
     */
    public void updateDomain(User user, UpdateUserDto dto) {
        if (dto == null || user == null) {
            return;
        }
        
        if (dto.getNombre() != null) {
            user.setNombre(dto.getNombre());
        }
        
        if (dto.getApellido() != null) {
            user.setApellido(dto.getApellido());
        }
        
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        
        if (dto.getTelefono() != null) {
            user.setTelefono(dto.getTelefono());
        }
        
        if (dto.getFechaNacimiento() != null) {
            user.setFechaNacimiento(dto.getFechaNacimiento());
        }
    }
}
