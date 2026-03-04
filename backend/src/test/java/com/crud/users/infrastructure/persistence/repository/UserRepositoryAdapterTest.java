package com.crud.users.infrastructure.persistence.repository;

import com.crud.users.domain.model.User;
import com.crud.users.infrastructure.persistence.entity.UserEntity;
import com.crud.users.infrastructure.persistence.mapper.UserEntityMapper;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para UserRepositoryAdapter.
 * Verifica la integración entre el repositorio JPA y el mapper.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepositoryAdapter Tests")
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserRepositoryAdapter userRepositoryAdapter;

    private UserEntity mockEntity;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockEntity = new UserEntity(
                "123",
                "Juan",
                "Pérez",
                "juan@example.com",
                "+57 300 123 4567",
                LocalDate.of(1990, 1, 1)
        );

        mockUser = new User(
                "123",
                "Juan",
                "Pérez",
                "juan@example.com",
                "+57 300 123 4567",
                LocalDate.of(1990, 1, 1)
        );
    }

    // ==================== HAPPY PATHS ====================

    @Test
    @DisplayName("findAll - retorna lista de usuarios mapeados")
    void findAll_shouldReturnMappedUsersList() {
        // Given
        UserEntity entity2 = new UserEntity(
                "456",
                "María",
                "García",
                "maria@example.com",
                "+57 310 456 7890",
                LocalDate.of(1995, 5, 15)
        );
        User user2 = new User(
                "456",
                "María",
                "García",
                "maria@example.com",
                "+57 310 456 7890",
                LocalDate.of(1995, 5, 15)
        );

        when(userJpaRepository.findAll()).thenReturn(Arrays.asList(mockEntity, entity2));
        when(userEntityMapper.toDomain(mockEntity)).thenReturn(mockUser);
        when(userEntityMapper.toDomain(entity2)).thenReturn(user2);

        // When
        List<User> users = userRepositoryAdapter.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users.get(0)).isEqualTo(mockUser);
        assertThat(users.get(1)).isEqualTo(user2);
        verify(userJpaRepository).findAll();
        verify(userEntityMapper, times(2)).toDomain(any(UserEntity.class));
    }

    @Test
    @DisplayName("findById - retorna usuario mapeado cuando existe")
    void findById_shouldReturnMappedUserWhenExists() {
        // Given
        when(userJpaRepository.findById("123")).thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toDomain(mockEntity)).thenReturn(mockUser);

        // When
        Optional<User> result = userRepositoryAdapter.findById("123");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockUser);
        verify(userJpaRepository).findById("123");
        verify(userEntityMapper).toDomain(mockEntity);
    }

    @Test
    @DisplayName("findByEmail - encuentra usuario case-insensitive")
    void findByEmail_shouldFindUserCaseInsensitive() {
        // Given
        when(userJpaRepository.findByEmailIgnoreCase("JUAN@EXAMPLE.COM"))
                .thenReturn(Optional.of(mockEntity));
        when(userEntityMapper.toDomain(mockEntity)).thenReturn(mockUser);

        // When
        Optional<User> result = userRepositoryAdapter.findByEmail("JUAN@EXAMPLE.COM");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockUser);
        verify(userJpaRepository).findByEmailIgnoreCase("JUAN@EXAMPLE.COM");
    }

    @Test
    @DisplayName("save - guarda y retorna usuario mapeado")
    void save_shouldSaveAndReturnMappedUser() {
        // Given
        UserEntity savedEntity = new UserEntity(
                "123",
                "Juan",
                "Pérez",
                "juan@example.com",
                "+57 300 123 4567",
                LocalDate.of(1990, 1, 1)
        );

        when(userEntityMapper.toEntity(mockUser)).thenReturn(mockEntity);
        when(userJpaRepository.save(mockEntity)).thenReturn(savedEntity);
        when(userEntityMapper.toDomain(savedEntity)).thenReturn(mockUser);

        // When
        User result = userRepositoryAdapter.save(mockUser);

        // Then
        assertThat(result).isEqualTo(mockUser);
        verify(userEntityMapper).toEntity(mockUser);
        verify(userJpaRepository).save(mockEntity);
        verify(userEntityMapper).toDomain(savedEntity);
    }

    @Test
    @DisplayName("deleteById - delega correctamente al repositorio JPA")
    void deleteById_shouldDelegateToJpaRepository() {
        // When
        userRepositoryAdapter.deleteById("123");

        // Then
        verify(userJpaRepository).deleteById("123");
    }

    @Test
    @DisplayName("existsByEmail - verifica existencia case-insensitive")
    void existsByEmail_shouldCheckExistenceCaseInsensitive() {
        // Given
        when(userJpaRepository.existsByEmailIgnoreCase("juan@example.com")).thenReturn(true);

        // When
        boolean exists = userRepositoryAdapter.existsByEmail("juan@example.com");

        // Then
        assertThat(exists).isTrue();
        verify(userJpaRepository).existsByEmailIgnoreCase("juan@example.com");
    }

    @Test
    @DisplayName("searchByTerm - retorna usuarios filtrados y mapeados")
    void searchByTerm_shouldReturnFilteredAndMappedUsers() {
        // Given
        when(userJpaRepository.searchByTerm("Juan")).thenReturn(Arrays.asList(mockEntity));
        when(userEntityMapper.toDomain(mockEntity)).thenReturn(mockUser);

        // When
        List<User> results = userRepositoryAdapter.searchByTerm("Juan");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isEqualTo(mockUser);
        verify(userJpaRepository).searchByTerm("Juan");
        verify(userEntityMapper).toDomain(mockEntity);
    }

    // ==================== SAD PATHS ====================

    @Test
    @DisplayName("findById - retorna empty cuando no existe")
    void findById_shouldReturnEmptyWhenNotFound() {
        // Given
        when(userJpaRepository.findById("999")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepositoryAdapter.findById("999");

        // Then
        assertThat(result).isEmpty();
        verify(userJpaRepository).findById("999");
        verify(userEntityMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("findByEmail - retorna empty cuando email no existe")
    void findByEmail_shouldReturnEmptyWhenEmailNotFound() {
        // Given
        when(userJpaRepository.findByEmailIgnoreCase("noexiste@example.com"))
                .thenReturn(Optional.empty());

        // When
        Optional<User> result = userRepositoryAdapter.findByEmail("noexiste@example.com");

        // Then
        assertThat(result).isEmpty();
        verify(userJpaRepository).findByEmailIgnoreCase("noexiste@example.com");
    }

    @Test
    @DisplayName("existsByEmail - retorna false cuando email no existe")
    void existsByEmail_shouldReturnFalseWhenEmailNotFound() {
        // Given
        when(userJpaRepository.existsByEmailIgnoreCase("noexiste@example.com")).thenReturn(false);

        // When
        boolean exists = userRepositoryAdapter.existsByEmail("noexiste@example.com");

        // Then
        assertThat(exists).isFalse();
        verify(userJpaRepository).existsByEmailIgnoreCase("noexiste@example.com");
    }

    @Test
    @DisplayName("searchByTerm - retorna lista vacía cuando no hay resultados")
    void searchByTerm_shouldReturnEmptyListWhenNoResults() {
        // Given
        when(userJpaRepository.searchByTerm("NoExiste")).thenReturn(Arrays.asList());

        // When
        List<User> results = userRepositoryAdapter.searchByTerm("NoExiste");

        // Then
        assertThat(results).isEmpty();
        verify(userJpaRepository).searchByTerm("NoExiste");
        verify(userEntityMapper, never()).toDomain(any());
    }
}
