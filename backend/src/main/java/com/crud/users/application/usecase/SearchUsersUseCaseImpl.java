package com.crud.users.application.usecase;

import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.application.port.in.SearchUsersUseCase;
import com.crud.users.application.port.out.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Caso de uso: buscar usuarios por término.
 */
@Service
@Transactional(readOnly = true)
public class SearchUsersUseCaseImpl implements SearchUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserDtoMapper userDtoMapper;

    public SearchUsersUseCaseImpl(UserRepositoryPort userRepositoryPort, UserDtoMapper userDtoMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public List<UserResponseDto> execute(String searchTerm) {
        return userRepositoryPort.searchByTerm(searchTerm).stream()
                .map(userDtoMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
