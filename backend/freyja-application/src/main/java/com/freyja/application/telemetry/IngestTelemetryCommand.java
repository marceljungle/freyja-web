package com.freyja.application.telemetry;

import java.time.Instant;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

/**
 * Input for {@link IngestTelemetryUseCase}, produced by the MQTT adapter from a
 * firmware payload. Carries the health metrics ({@code rsrp/trackedSvs/svsUsed/cn0})
 * that the firmware sends with every report, the serving-cell identifiers for a
 * fallback location when there is no GPS fix, and the {@code buffered} flag for
 * historical fixes replayed from the device's offline buffer.
 */
public record IngestTelemetryCommand(
    String imei,
    String reason,
    boolean hasFix,
    boolean buffered,
    Double latitude,
    Double longitude,
    Double accuracy,
    Integer batteryMv,
    Double temperatureC,
    Integer rsrp,
    Integer trackedSvs,
    Integer svsUsed,
    Double cn0,
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
