package com.freyja.domain.vo;

import com.freyja.domain.exception.ValidationException;

public record Coordinates(double latitude, double longitude) {

  public Coordinates {
    if (latitude < -90.0 || latitude > 90.0) {
      throw new ValidationException("Latitude must be between -90 and 90");
    }
    if (longitude < -180.0 || longitude > 180.0) {
      throw new ValidationException("Longitude must be between -180 and 180");
    }
  }

  public static Coordinates of(double latitude, double longitude) {
    return new Coordinates(latitude, longitude);
  }
}
