package com.crud.users.application.usecase;

import com.crud.users.application.dto.CreateUserDto;
import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.domain.port.out.UserRepositoryPort;
import com.crud.users.domain.exception.EmailAlreadyExistsException;
import com.crud.users.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase - Unit Tests")
class CreateUserUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private CreateUserUseCaseImpl useCase;

    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto();
        createUserDto.setNombre("Pedro");
        createUserDto.setApellido("López");
        createUserDto.setEmail("pedro@example.com");
        createUserDto.setTelefono("+57 320 789 1234");
        createUserDto.setFechaNacimiento(LocalDate.of(1988, 3, 20));
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUser() {
        User newUser = new User("2", "Pedro", "López", "pedro@example.com", "+57 320 789 1234", LocalDate.of(1988, 3, 20));
        UserResponseDto responseDto = new UserResponseDto("2", "Pedro", "López", "pedro@example.com", "+57 320 789 1234", "1988-03-20");

        given(userRepositoryPort.existsByEmail(anyString())).willReturn(false);
        given(userDtoMapper.toDomain(createUserDto)).willReturn(newUser);
        given(userRepositoryPort.save(any(User.class))).willReturn(newUser);
        given(userDtoMapper.toResponseDto(newUser)).willReturn(responseDto);

        UserResponseDto result = useCase.execute(createUserDto);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Pedro");
        verify(userRepositoryPort).existsByEmail("pedro@example.com");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email is taken")
    void shouldThrowExceptionWhenEmailExists() {
        given(userRepositoryPort.existsByEmail("pedro@example.com")).willReturn(true);

        assertThatThrownBy(() -> useCase.execute(createUserDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("pedro@example.com");

        verify(userRepositoryPort, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when age exceeds 150 years")
    void shouldThrowExceptionWhenAgeExceeds150Years() {
        createUserDto.setFechaNacimiento(LocalDate.of(1800, 1, 1));

        assertThatThrownBy(() -> useCase.execute(createUserDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("150 años");

        verify(userRepositoryPort, never()).save(any(User.class));
    }
}
