package com.freyja.application.user;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

public record AuthenticateUserCommand(String email, String password) implements UseCaseInput {

  @Override
  public void validate() {
    if (email == null || email.isBlank()) {
      throw new ValidationException("Email must not be blank");
    }
    if (password == null || password.isBlank()) {
      throw new ValidationException("Password must not be blank");
    }
  }
}
