package com.freyja.application.telemetry;

import java.time.Instant;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

/**
 * Input for {@link IngestTelemetryUseCase}, produced by the MQTT adapter from a
 * firmware payload. When {@code hasFix} is false the coordinates are absent, but
 * the serving-cell identifiers ({@code mcc/mnc/tac/cellId}) may be present for a
 * cell-tower location fallback.
 */
public record IngestTelemetryCommand(
    String imei,
    String reason,
    boolean hasFix,
    Double latitude,
    Double longitude,
    Double accuracy,
    Integer batteryMv,
    Double temperatureC,
    Integer mcc,
    Integer mnc,
    Integer tac,
    Integer cellId,
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

  /** True when all four cell identifiers are present and positive. */
  public boolean hasCellTower() {
    return mcc != null && mnc != null && tac != null && cellId != null
        && mcc > 0 && mnc > 0 && tac > 0 && cellId > 0;
  }
}
