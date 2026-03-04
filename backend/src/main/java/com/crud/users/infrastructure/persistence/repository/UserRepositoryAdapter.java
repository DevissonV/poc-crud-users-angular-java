package com.crud.users.infrastructure.persistence.repository;

import com.crud.users.domain.port.out.UserRepositoryPort;
import com.crud.users.domain.model.User;
import com.crud.users.infrastructure.persistence.mapper.UserEntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador (driven adapter) que implementa el puerto de salida UserRepositoryPort,
 * delegando a UserJpaRepository (Spring Data JPA).
 */
@Component
public class UserRepositoryAdapter implements UserRepositoryPort {
    
    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    public UserRepositoryAdapter(UserJpaRepository userJpaRepository, UserEntityMapper userEntityMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findById(String id) {
        return userJpaRepository.findById(id)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public User save(User user) {
        var entity = userEntityMapper.toEntity(user);
        var savedEntity = userJpaRepository.save(entity);
        return userEntityMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(String id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmailIgnoreCase(email);
    }

    @Override
    public List<User> searchByTerm(String searchTerm) {
        return userJpaRepository.searchByTerm(searchTerm).stream()
                .map(userEntityMapper::toDomain)
                .collect(Collectors.toList());
    }
}
