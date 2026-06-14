package com.freyja.application.telemetry;

import java.time.Instant;
import java.util.UUID;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

public record GetTrajectoryQuery(
    UUID ownerId,
    UUID deviceId,
    Instant from,
    Instant to,
    Integer limit) implements UseCaseInput {

  public static final int DEFAULT_LIMIT = 1000;

  public static final int MAX_LIMIT = 5000;

  @Override
  public void validate() {
    if (ownerId == null) {
      throw new ValidationException("Owner is required");
    }
    if (deviceId == null) {
      throw new ValidationException("Device id is required");
    }
    if (from != null && to != null && from.isAfter(to)) {
      throw new ValidationException("'from' must not be after 'to'");
    }
    if (limit != null && limit < 1) {
      throw new ValidationException("'limit' must be positive");
    }
  }
}
