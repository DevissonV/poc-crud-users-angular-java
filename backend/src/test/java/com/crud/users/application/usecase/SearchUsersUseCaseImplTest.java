package com.crud.users.application.usecase;

import com.crud.users.application.dto.UserResponseDto;
import com.crud.users.application.mapper.UserDtoMapper;
import com.crud.users.domain.port.out.UserRepositoryPort;
import com.crud.users.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchUsersUseCase - Unit Tests")
class SearchUsersUseCaseImplTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private SearchUsersUseCaseImpl useCase;

    private User mockUser;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        mockUser = new User("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", LocalDate.of(1990, 1, 15));
        userResponseDto = new UserResponseDto("1", "Juan", "Pérez", "juan@example.com", "+57 300 123 4567", "1990-01-15");
    }

    @Test
    @DisplayName("Should search users successfully")
    void shouldSearchUsers() {
        given(userRepositoryPort.searchByTerm("Juan")).willReturn(Arrays.asList(mockUser));
        given(userDtoMapper.toResponseDto(any(User.class))).willReturn(userResponseDto);

        List<UserResponseDto> result = useCase.execute("Juan");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Juan");
        verify(userRepositoryPort).searchByTerm("Juan");
    }

    @Test
    @DisplayName("Should return empty list when no users match search term")
    void shouldReturnEmptyListWhenNoUsersMatch() {
        given(userRepositoryPort.searchByTerm("nonexistent")).willReturn(Arrays.asList());

        List<UserResponseDto> result = useCase.execute("nonexistent");

        assertThat(result).isEmpty();
        verify(userRepositoryPort).searchByTerm("nonexistent");
    }
}
