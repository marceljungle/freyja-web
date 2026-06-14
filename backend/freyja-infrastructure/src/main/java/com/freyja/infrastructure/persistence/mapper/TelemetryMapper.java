package com.freyja.infrastructure.persistence.mapper;

import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.Coordinates;
import com.freyja.infrastructure.persistence.entity.TelemetryEntity;

public final class TelemetryMapper {

  private TelemetryMapper() {
  }

  public static TelemetryData toDomain(TelemetryEntity e) {
    Coordinates coordinates = null;
    if (e.getLatitude() != null && e.getLongitude() != null) {
      coordinates = Coordinates.of(e.getLatitude(), e.getLongitude());
    }
    BatteryLevel battery = e.getBatteryMv() != null
        ? BatteryLevel.ofMillivolts(e.getBatteryMv())
        : null;
    return new TelemetryData(
        e.getId(),
        e.getDeviceId(),
        e.getReason(),
        e.isHasFix(),
        coordinates,
        e.getAccuracy(),
        battery,
        e.getDeviceTime(),
        e.getReceivedAt());
  }

  public static TelemetryEntity toEntity(TelemetryData t) {
    TelemetryEntity e = new TelemetryEntity();
    e.setId(t.id());
    e.setDeviceId(t.deviceId());
    e.setReason(t.reason());
    e.setHasFix(t.hasFix());
    t.coordinates().ifPresent(c -> {
      e.setLatitude(c.latitude());
      e.setLongitude(c.longitude());
    });
    e.setAccuracy(t.accuracy().orElse(null));
    e.setBatteryMv(t.battery().map(BatteryLevel::millivolts).orElse(null));
    e.setDeviceTime(t.deviceTime().orElse(null));
    e.setReceivedAt(t.receivedAt());
    return e;
  }
}
