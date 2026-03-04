package com.crud.users.application.usecase;

import com.crud.users.application.port.in.DeleteUserUseCase;
import com.crud.users.application.port.out.UserRepositoryPort;
import com.crud.users.domain.exception.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso: eliminar un usuario.
 */
@Service
@Transactional
public class DeleteUserUseCaseImpl implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public DeleteUserUseCaseImpl(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void execute(String id) {
        // Verificar que el usuario existe
        if (!userRepositoryPort.findById(id).isPresent()) {
            throw new UserNotFoundException(id);
        }

        userRepositoryPort.deleteById(id);
    }
}
