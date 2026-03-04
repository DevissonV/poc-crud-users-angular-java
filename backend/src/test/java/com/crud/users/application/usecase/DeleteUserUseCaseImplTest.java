package com.crud.users.application.usecase;

import com.crud.users.application.port.out.UserRepositoryPort;
import com.crud.users.domain.exception.UserNotFoundException;
import com.crud.users.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteUserUseCase - Unit Tests")
class DeleteUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private DeleteUserUseCaseImpl useCase;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", LocalDate.of(1990, 1, 15));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUser() {
        given(userRepositoryPort.findById("1")).willReturn(Optional.of(mockUser));
        doNothing().when(userRepositoryPort).deleteById("1");

        useCase.execute("1");

        verify(userRepositoryPort).findById("1");
        verify(userRepositoryPort).deleteById("1");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        given(userRepositoryPort.findById("999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("999"))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepositoryPort, never()).deleteById(anyString());
    }
}
