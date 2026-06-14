package com.freyja.infrastructure.rest.dto;

import java.time.Instant;
import java.util.UUID;

import com.freyja.application.user.AuthenticationResult;

public record AuthResponse(String token, Instant expiresAt, UserResponse user) {

  public static AuthResponse from(AuthenticationResult result) {
    return new AuthResponse(
        result.token(),
        result.expiresAt(),
        new UserResponse(result.userId(), result.email(), result.displayName(), result.role()));
  }

  public record UserResponse(UUID id, String email, String displayName, String role) {

  }
}
