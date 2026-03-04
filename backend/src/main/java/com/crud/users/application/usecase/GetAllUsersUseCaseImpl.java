package com.crud.users.application.usecase;

import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.application.port.in.GetAllUsersUseCase;
import com.crud.users.domain.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso: obtener todos los usuarios.
 */
@Service
@Transactional(readOnly = true)
public class GetAllUsersUseCaseImpl implements GetAllUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserDtoMapper userDtoMapper;

    public GetAllUsersUseCaseImpl(UserRepositoryPort userRepositoryPort, UserDtoMapper userDtoMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public List<UserResponseDto> execute() {
        return userRepositoryPort.findAll().stream()
                .map(userDtoMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
