package com.freyja.domain.model.telemetry;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.CellTower;
import com.freyja.domain.vo.Coordinates;

/**
 * A single telemetry report from a device, mirroring the firmware MQTT payload.
 *
 * <p>Location may come from a GNSS fix ({@code hasFix == true}) or, when GPS
 * fails, from a cell-tower lookup ({@code approximate == true}); in the latter
 * case the coordinates are imprecise and {@code accuracy} reflects the cell
 * range. With neither, the coordinates are absent.
 */
public class TelemetryData {

  private final Long id;

  private final UUID deviceId;

  private final String reason;

  private final boolean hasFix;

  private final Coordinates coordinates;   // GPS or cell-resolved; null if unknown

  private final Double accuracy;           // metres; null when no location

  private final boolean approximate;       // true when coordinates came from a cell tower

  private final BatteryLevel battery;      // null if not reported

  private final Double temperatureC;       // null if not reported

  private final CellTower cellTower;       // serving cell (when no GPS fix), nullable

  private final Instant deviceTime;        // GNSS UTC, null without a fix

  private final Instant receivedAt;

  public TelemetryData(Long id, UUID deviceId, String reason, boolean hasFix,
      Coordinates coordinates, Double accuracy, boolean approximate,
      BatteryLevel battery, Double temperatureC, CellTower cellTower,
      Instant deviceTime, Instant receivedAt) {
    this.id = id;
    this.deviceId = Objects.requireNonNull(deviceId, "deviceId");
    this.reason = reason;
    this.hasFix = hasFix;
    this.coordinates = coordinates;
    this.accuracy = accuracy;
    this.approximate = approximate;
    this.battery = battery;
    this.temperatureC = temperatureC;
    this.cellTower = cellTower;
    this.deviceTime = deviceTime;
    this.receivedAt = receivedAt;
  }

  /**
   * Factory for a reading that carries a valid GNSS fix.
   */
  public static TelemetryData withFix(UUID deviceId, String reason, Coordinates coordinates,
      Double accuracy, BatteryLevel battery, Double temperatureC,
      Instant deviceTime, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, true,
        Objects.requireNonNull(coordinates, "coordinates"),
        accuracy, false, battery, temperatureC, null, deviceTime, receivedAt);
  }

  /**
   * Factory for a reading whose location was resolved from a cell tower (no GPS).
   */
  public static TelemetryData withApproximateLocation(UUID deviceId, String reason,
      Coordinates coordinates, Double accuracyMeters, BatteryLevel battery,
      Double temperatureC, CellTower cellTower, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, false,
        Objects.requireNonNull(coordinates, "coordinates"),
        accuracyMeters, true, battery, temperatureC, cellTower, null, receivedAt);
  }

  /**
   * Factory for a reading with no location (no GPS fix and no/failed cell lookup).
   */
  public static TelemetryData withoutLocation(UUID deviceId, String reason,
      BatteryLevel battery, Double temperatureC, CellTower cellTower, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, false,
        null, null, false, battery, temperatureC, cellTower, null, receivedAt);
  }

  public Long id() {
    return id;
  }

  public UUID deviceId() {
    return deviceId;
  }

  public String reason() {
    return reason;
  }

  public boolean hasFix() {
    return hasFix;
  }

  public boolean approximate() {
    return approximate;
  }

  public Optional<Coordinates> coordinates() {
    return Optional.ofNullable(coordinates);
  }

  public Optional<Double> accuracy() {
    return Optional.ofNullable(accuracy);
  }

  public Optional<BatteryLevel> battery() {
    return Optional.ofNullable(battery);
  }

  public Optional<Double> temperatureC() {
    return Optional.ofNullable(temperatureC);
  }

  public Optional<CellTower> cellTower() {
    return Optional.ofNullable(cellTower);
  }

  public Optional<Instant> deviceTime() {
    return Optional.ofNullable(deviceTime);
  }

  public Instant receivedAt() {
    return receivedAt;
  }
}
