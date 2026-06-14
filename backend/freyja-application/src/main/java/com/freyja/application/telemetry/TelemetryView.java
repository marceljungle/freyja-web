package com.freyja.application.telemetry;

import java.time.Instant;
import java.util.UUID;

import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.Coordinates;

public record TelemetryView(
    Long id,
    UUID deviceId,
    String reason,
    boolean hasFix,
    Double latitude,
    Double longitude,
    Double accuracy,
    Integer batteryMv,
    Integer batteryPercent,
    Instant deviceTime,
    Instant receivedAt) {

  public static TelemetryView from(TelemetryData t) {
    Coordinates c = t.coordinates().orElse(null);
    BatteryLevel b = t.battery().orElse(null);
    return new TelemetryView(
        t.id(),
        t.deviceId(),
        t.reason(),
        t.hasFix(),
        c != null ? c.latitude() : null,
        c != null ? c.longitude() : null,
        t.accuracy().orElse(null),
        b != null ? b.millivolts() : null,
        b != null ? b.percentage() : null,
        t.deviceTime().orElse(null),
        t.receivedAt());
  }
}
