package com.freyja.application.telemetry;

import java.util.UUID;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

public record GetLatestTelemetryQuery(UUID ownerId, UUID deviceId) implements UseCaseInput {

  @Override
  public void validate() {
    if (ownerId == null) {
      throw new ValidationException("Owner is required");
    }
    if (deviceId == null) {
      throw new ValidationException("Device id is required");
    }
  }
}
