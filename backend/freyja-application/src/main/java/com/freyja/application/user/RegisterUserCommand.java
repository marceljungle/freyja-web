package com.freyja.application.user;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

public record RegisterUserCommand(String email, String password, String displayName)
    implements UseCaseInput {

  private static final int MIN_PASSWORD_LENGTH = 8;

  private static final int MAX_PASSWORD_LENGTH = 100;

  @Override
  public void validate() {
    if (email == null || email.isBlank()) {
      throw new ValidationException("Email must not be blank");
    }
    if (password == null || password.isBlank()) {
      throw new ValidationException("Password must not be blank");
    }
    if (password.length() < MIN_PASSWORD_LENGTH) {
      throw new ValidationException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
    }
    if (password.length() > MAX_PASSWORD_LENGTH) {
      throw new ValidationException("Password must be at most " + MAX_PASSWORD_LENGTH + " characters");
    }
  }
}
