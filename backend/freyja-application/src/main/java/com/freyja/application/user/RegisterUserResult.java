package com.freyja.application.user;

import java.util.UUID;

import com.freyja.domain.model.user.User;

public record RegisterUserResult(UUID userId, String email, String displayName) {

  public static RegisterUserResult from(User user) {
    return new RegisterUserResult(user.id(), user.email().value(), user.displayName());
  }
}
