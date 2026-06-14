package com.freyja.infrastructure.persistence.mapper;

import com.freyja.domain.model.user.User;
import com.freyja.domain.vo.Email;
import com.freyja.infrastructure.persistence.entity.UserEntity;

public final class UserMapper {

  private UserMapper() {
  }

  public static User toDomain(UserEntity e) {
    return new User(
        e.getId(),
        Email.of(e.getEmail()),
        e.getPasswordHash(),
        e.getDisplayName(),
        e.getRole(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  public static UserEntity toEntity(User user) {
    UserEntity e = new UserEntity();
    e.setId(user.id());
    e.setEmail(user.email().value());
    e.setPasswordHash(user.passwordHash());
    e.setDisplayName(user.displayName());
    e.setRole(user.role());
    e.setCreatedAt(user.createdAt());
    e.setUpdatedAt(user.updatedAt());
    return e;
  }
}
