package com.freyja.domain.vo;

import java.util.regex.Pattern;

import com.freyja.domain.exception.ValidationException;

public record Imei(String value) {

  private static final Pattern PATTERN = Pattern.compile("^\\d{15,16}$");

  public Imei {
    if (value == null) {
      throw new ValidationException("IMEI must not be null");
    }
    value = value.trim();
    if (!PATTERN.matcher(value).matches()) {
      throw new ValidationException("IMEI must be 15-16 digits");
    }
  }

  public static Imei of(String value) {
    return new Imei(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
