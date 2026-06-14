package com.freyja.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.user.User;
import com.freyja.domain.port.out.UserRepository;
import com.freyja.domain.vo.Email;
import com.freyja.infrastructure.persistence.mapper.UserMapper;
import com.freyja.infrastructure.persistence.repository.SpringDataUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

  private final SpringDataUserRepository repository;

  public UserRepositoryAdapter(SpringDataUserRepository repository) {
    this.repository = repository;
  }

  @Override
  public User save(User user) {
    return UserMapper.toDomain(repository.save(UserMapper.toEntity(user)));
  }

  @Override
  public Optional<User> findById(UUID id) {
    return repository.findById(id).map(UserMapper::toDomain);
  }

  @Override
  public Optional<User> findByEmail(Email email) {
    return repository.findByEmail(email.value()).map(UserMapper::toDomain);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return repository.existsByEmail(email.value());
  }
}
