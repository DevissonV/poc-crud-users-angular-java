package com.crud.users.application.usecase;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.application.port.in.CreateUserUseCase;
import com.crud.users.application.port.out.UserRepositoryPort;
import com.crud.users.domain.exception.EmailAlreadyExistsException;
import com.crud.users.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

/**
 * Caso de uso: crear un nuevo usuario.
 */
@Service
@Transactional
public class CreateUserUseCaseImpl implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserDtoMapper userDtoMapper;

    public CreateUserUseCaseImpl(UserRepositoryPort userRepositoryPort, UserDtoMapper userDtoMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserResponseDto execute(CreateUserDto createUserDto) {
        // Validar email único (case-insensitive)
        if (userRepositoryPort.existsByEmail(createUserDto.getEmail())) {
            throw new EmailAlreadyExistsException(createUserDto.getEmail());
        }

        // Validar edad máxima (no más de 150 años)
        validateMaxAge(createUserDto.getFechaNacimiento());

        // Convertir DTO a entidad de dominio y guardar
        User user = userDtoMapper.toDomain(createUserDto);
        User savedUser = userRepositoryPort.save(user);

        return userDtoMapper.toResponseDto(savedUser);
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
