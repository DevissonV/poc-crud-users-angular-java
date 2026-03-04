package com.crud.users.application.usecase;

import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.application.port.in.UpdateUserUseCase;
import com.crud.users.domain.port.out.UserRepositoryPort;
import com.crud.users.domain.exception.EmailAlreadyExistsException;
import com.crud.users.domain.exception.UserNotFoundException;
import com.crud.users.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * Caso de uso: actualizar un usuario existente.
 */
@Service
@Transactional
public class UpdateUserUseCaseImpl implements UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserDtoMapper userDtoMapper;

    public UpdateUserUseCaseImpl(UserRepositoryPort userRepositoryPort, UserDtoMapper userDtoMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserResponseDto execute(String id, UpdateUserDto updateUserDto) {
        // Verificar que el usuario existe
        User existingUser = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // Si se actualiza el email, validar que no exista otro usuario con ese email
        if (updateUserDto.getEmail() != null && !updateUserDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            if (userRepositoryPort.existsByEmail(updateUserDto.getEmail())) {
                throw new EmailAlreadyExistsException(updateUserDto.getEmail());
            }
        }

        // Validar edad máxima si se actualiza fecha de nacimiento
        if (updateUserDto.getFechaNacimiento() != null) {
            validateMaxAge(updateUserDto.getFechaNacimiento());
        }

        // Actualizar campos no nulos
        userDtoMapper.updateDomain(existingUser, updateUserDto);

        // Guardar cambios
        User updatedUser = userRepositoryPort.save(existingUser);

        return userDtoMapper.toResponseDto(updatedUser);
    }

    private void validateMaxAge(LocalDate fechaNacimiento) {
        if (fechaNacimiento != null) {
            Period period = Period.between(fechaNacimiento, LocalDate.now());
            if (period.getYears() > 150) {
                throw new IllegalArgumentException("La fecha de nacimiento no puede ser mayor a 150 años");
            }
        }
    }
}
