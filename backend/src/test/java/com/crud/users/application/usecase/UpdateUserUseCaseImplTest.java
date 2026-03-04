package com.crud.users.application.usecase;

import com.crud.users.application.dto.UpdateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.application.port.out.UserRepositoryPort;
import com.crud.users.domain.exception.EmailAlreadyExistsException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateUserUseCase - Unit Tests")
class UpdateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UpdateUserUseCaseImpl useCase;

    private User mockUser;
    private UpdateUserDto updateUserDto;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        mockUser = new User("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", LocalDate.of(1990, 1, 15));
        updateUserDto = new UpdateUserDto();
        updateUserDto.setNombre("Juan Actualizado");
        userResponseDto = new UserResponseDto("1", "Juan Actualizado", "Pérez", "juan@example.com", "+57 300 123 4567", "1990-01-15");
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUser() {
        given(userRepositoryPort.findById("1")).willReturn(Optional.of(mockUser));
        given(userRepositoryPort.save(any(User.class))).willReturn(mockUser);
        given(userDtoMapper.toResponseDto(mockUser)).willReturn(userResponseDto);
        doNothing().when(userDtoMapper).updateDomain(mockUser, updateUserDto);

        UserResponseDto result = useCase.execute("1", updateUserDto);

        assertThat(result).isNotNull();
        verify(userRepositoryPort).findById("1");
        verify(userDtoMapper).updateDomain(mockUser, updateUserDto);
        verify(userRepositoryPort).save(mockUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user does not exist")
    void shouldThrowExceptionWhenUserNotFound() {
        given(userRepositoryPort.findById("999")).willReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute("999", updateUserDto))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when new email is taken")
    void shouldThrowExceptionWhenUpdatingWithExistingEmail() {
        updateUserDto.setEmail("otro@example.com");
        given(userRepositoryPort.findById("1")).willReturn(Optional.of(mockUser));
        given(userRepositoryPort.existsByEmail("otro@example.com")).willReturn(true);

        assertThatThrownBy(() -> useCase.execute("1", updateUserDto))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should allow updating with the same email")
    void shouldAllowUpdatingWithSameEmail() {
        updateUserDto.setEmail("juan@example.com");
        given(userRepositoryPort.findById("1")).willReturn(Optional.of(mockUser));
        given(userRepositoryPort.save(any(User.class))).willReturn(mockUser);
        given(userDtoMapper.toResponseDto(mockUser)).willReturn(userResponseDto);

        UserResponseDto result = useCase.execute("1", updateUserDto);

        assertThat(result).isNotNull();
        verify(userRepositoryPort, never()).existsByEmail(anyString());
        verify(userRepositoryPort).save(mockUser);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when birth date exceeds 150 years")
    void shouldThrowExceptionWhenAgeExceeds150Years() {
        updateUserDto.setFechaNacimiento(LocalDate.of(1800, 1, 1));
        given(userRepositoryPort.findById("1")).willReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> useCase.execute("1", updateUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("150 años");

        verify(userRepositoryPort, never()).save(any(User.class));
    }
}
