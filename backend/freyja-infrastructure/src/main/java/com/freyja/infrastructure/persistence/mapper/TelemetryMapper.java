package com.freyja.infrastructure.persistence.mapper;

import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.CellTower;
import com.freyja.domain.vo.Coordinates;
import com.freyja.domain.vo.HealthMetrics;
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
    CellTower cellTower = null;
    if (e.getMcc() != null && e.getMnc() != null && e.getTac() != null && e.getCellId() != null) {
      cellTower = CellTower.of(e.getMcc(), e.getMnc(), e.getTac(), e.getCellId());
    }
    HealthMetrics health = HealthMetrics.of(e.getRsrpDbm(), e.getTrackedSvs(), e.getSvsUsed(), e.getCn0());
    return new TelemetryData(
        e.getId(),
        e.getDeviceId(),
        e.getReason(),
        e.isHasFix(),
        coordinates,
        e.getAccuracy(),
        e.isApproximate(),
        battery,
        e.getTemperatureC(),
        health,
        cellTower,
        e.getDeviceTime(),
        e.isBuffered(),
        e.getReceivedAt());
  }

  public static TelemetryEntity toEntity(TelemetryData t) {
    TelemetryEntity e = new TelemetryEntity();
    e.setId(t.id());
    e.setDeviceId(t.deviceId());
    e.setReason(t.reason());
    e.setHasFix(t.hasFix());
    e.setApproximate(t.approximate());
    e.setBuffered(t.buffered());
    t.coordinates().ifPresent(c -> {
      e.setLatitude(c.latitude());
      e.setLongitude(c.longitude());
    });
    e.setAccuracy(t.accuracy().orElse(null));
    e.setBatteryMv(t.battery().map(BatteryLevel::millivolts).orElse(null));
    e.setTemperatureC(t.temperatureC().orElse(null));
    HealthMetrics health = t.health();
    e.setRsrpDbm(health.rsrpDbm());
    e.setTrackedSvs(health.trackedSvs());
    e.setSvsUsed(health.svsUsed());
    e.setCn0(health.cn0());
    t.cellTower().ifPresent(cell -> {
      e.setMcc(cell.mcc());
      e.setMnc(cell.mnc());
      e.setTac(cell.tac());
      e.setCellId(cell.cellId());
    });
    e.setDeviceTime(t.deviceTime().orElse(null));
    e.setReceivedAt(t.receivedAt());
    return e;
  }
}
