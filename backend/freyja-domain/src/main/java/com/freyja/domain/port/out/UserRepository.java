package com.freyja.domain.port.out;

import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.user.User;
import com.freyja.domain.vo.Email;

public interface UserRepository {

  User save(User user);

  Optional<User> findById(UUID id);

  Optional<User> findByEmail(Email email);

  boolean existsByEmail(Email email);
}
