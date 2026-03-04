package com.crud.users.application.usecase;

import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.application.port.in.GetUserByIdUseCase;
import com.crud.users.application.port.out.UserRepositoryPort;
import com.crud.users.domain.exception.UserNotFoundException;
import com.crud.users.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: obtener un usuario por ID.
 */
@Service
@Transactional(readOnly = true)
public class GetUserByIdUseCaseImpl implements GetUserByIdUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final UserDtoMapper userDtoMapper;

    public GetUserByIdUseCaseImpl(UserRepositoryPort userRepositoryPort, UserDtoMapper userDtoMapper) {
        this.userRepositoryPort = userRepositoryPort;
        this.userDtoMapper = userDtoMapper;
    }

    @Override
    public UserResponseDto execute(String id) {
        User user = userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userDtoMapper.toResponseDto(user);
    }
}
