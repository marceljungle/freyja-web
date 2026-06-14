package com.freyja.application.user;

import java.time.Instant;
import java.util.UUID;

import com.freyja.domain.model.user.User;
import com.freyja.domain.vo.IssuedToken;

public record AuthenticationResult(
    String token,
    Instant expiresAt,
    UUID userId,
    String email,
    String displayName,
    String role) {

  public static AuthenticationResult of(User user, IssuedToken token) {
    return new AuthenticationResult(
        token.token(),
        token.expiresAt(),
        user.id(),
        user.email().value(),
        user.displayName(),
        user.role().name());
  }
}
