package com.crud.users.application.usecase;

import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserByIdUseCase - Unit Tests")
class GetUserByIdUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private GetUserByIdUseCaseImpl useCase;

    private User mockUser;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        mockUser = new User("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", LocalDate.of(1990, 1, 15));
        userResponseDto = new UserResponseDto("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", "1990-01-15");
    }

    @Test
    @DisplayName("Should return user by id successfully")
    void shouldGetUserById() {
        given(userRepositoryPort.findById("1")).willReturn(Optional.of(mockUser));
        given(userDtoMapper.toResponseDto(mockUser)).willReturn(userResponseDto);

        UserResponseDto result = useCase.execute("1");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getNombre()).isEqualTo("Juan");
        verify(userRepositoryPort).findById("1");
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        given(userRepositoryPort.findById("999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("999"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");
    }
}
