package com.freyja.domain.vo;

import java.util.regex.Pattern;

import com.freyja.domain.exception.ValidationException;

public record Email(String value) {

  private static final Pattern PATTERN =
      Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private static final int MAX_LENGTH = 255;

  public Email {
    if (value == null || value.isBlank()) {
      throw new ValidationException("Email must not be blank");
    }
    value = value.trim().toLowerCase();
    if (value.length() > MAX_LENGTH) {
      throw new ValidationException("Email must be at most " + MAX_LENGTH + " characters");
    }
    if (!PATTERN.matcher(value).matches()) {
      throw new ValidationException("Email format is invalid");
    }
  }

  public static Email of(String value) {
    return new Email(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
