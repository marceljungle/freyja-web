package com.freyja.application.telemetry;

import java.time.Instant;
import java.util.UUID;

import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.CellTower;
import com.freyja.domain.vo.Coordinates;
import com.freyja.domain.vo.HealthMetrics;

public record TelemetryView(
    Long id,
    UUID deviceId,
    String reason,
    boolean hasFix,
    boolean approximate,
    boolean buffered,
    Double latitude,
    Double longitude,
    Double accuracy,
    Integer batteryMv,
    Integer batteryPercent,
    Double temperatureC,
    Integer rsrp,
    Integer trackedSvs,
    Integer svsUsed,
    Double cn0,
    Integer mcc,
    Integer mnc,
    Integer tac,
    Integer cellId,
    Instant deviceTime,
    Instant receivedAt) {

  public static TelemetryView from(TelemetryData t) {
    Coordinates c = t.coordinates().orElse(null);
    BatteryLevel b = t.battery().orElse(null);
    CellTower cell = t.cellTower().orElse(null);
    HealthMetrics h = t.health();
    return new TelemetryView(
        t.id(),
        t.deviceId(),
        t.reason(),
        t.hasFix(),
        t.approximate(),
        t.buffered(),
        c != null ? c.latitude() : null,
        c != null ? c.longitude() : null,
        t.accuracy().orElse(null),
        b != null ? b.millivolts() : null,
        b != null ? b.percentage() : null,
        t.temperatureC().orElse(null),
        h.rsrpDbm(),
        h.trackedSvs(),
        h.svsUsed(),
        h.cn0(),
        cell != null ? cell.mcc() : null,
        cell != null ? cell.mnc() : null,
        cell != null ? cell.tac() : null,
        cell != null ? cell.cellId() : null,
        t.deviceTime().orElse(null),
        t.receivedAt());
  }
}
