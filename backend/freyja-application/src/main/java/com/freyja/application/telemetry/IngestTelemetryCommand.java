package com.freyja.application.telemetry;

import java.time.Instant;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

public record IngestTelemetryCommand(
    String imei,
    String reason,
    boolean hasFix,
    Double latitude,
    Double longitude,
    Double accuracy,
    Integer batteryMv,
    Instant deviceTime) implements UseCaseInput {

  @Override
  public void validate() {
    if (imei == null || imei.isBlank()) {
      throw new ValidationException("Telemetry is missing the device id (imei)");
    }
    if (hasFix && (latitude == null || longitude == null)) {
      throw new ValidationException("A fix must include latitude and longitude");
    }
  }
}
