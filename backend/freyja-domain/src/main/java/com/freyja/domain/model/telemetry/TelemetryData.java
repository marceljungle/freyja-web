package com.freyja.domain.model.telemetry;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.CellTower;
import com.freyja.domain.vo.Coordinates;
import com.freyja.domain.vo.HealthMetrics;

/**
 * A single telemetry report from a device, mirroring the firmware MQTT payload.
 *
 * <p>Location may come from a GNSS fix ({@code hasFix == true}) or, when GPS
 * fails, from a cell-tower lookup ({@code approximate == true}); in the latter
 * case the coordinates are imprecise and {@code accuracy} reflects the cell
 * range. With neither, the coordinates are absent. {@code buffered} marks a
 * historical fix replayed from the device's offline buffer.
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

  private final HealthMetrics health;      // radio/GNSS diagnostics (never null; may be EMPTY)

  private final CellTower cellTower;       // serving cell, nullable

  private final Instant deviceTime;        // GNSS UTC, null without a fix

  private final boolean buffered;          // true when replayed from the offline buffer

  private final Instant receivedAt;

  public TelemetryData(Long id, UUID deviceId, String reason, boolean hasFix,
      Coordinates coordinates, Double accuracy, boolean approximate,
      BatteryLevel battery, Double temperatureC, HealthMetrics health,
      CellTower cellTower, Instant deviceTime, boolean buffered, Instant receivedAt) {
    this.id = id;
    this.deviceId = Objects.requireNonNull(deviceId, "deviceId");
    this.reason = reason;
    this.hasFix = hasFix;
    this.coordinates = coordinates;
    this.accuracy = accuracy;
    this.approximate = approximate;
    this.battery = battery;
    this.temperatureC = temperatureC;
    this.health = health != null ? health : HealthMetrics.EMPTY;
    this.cellTower = cellTower;
    this.deviceTime = deviceTime;
    this.buffered = buffered;
    this.receivedAt = receivedAt;
  }

  /**
   * Factory for a reading that carries a valid GNSS fix. {@code buffered} is true
   * for a historical fix replayed from the device's offline buffer.
   */
  public static TelemetryData withFix(UUID deviceId, String reason, Coordinates coordinates,
      Double accuracy, BatteryLevel battery, Double temperatureC, HealthMetrics health,
      Instant deviceTime, boolean buffered, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, true,
        Objects.requireNonNull(coordinates, "coordinates"),
        accuracy, false, battery, temperatureC, health, null, deviceTime, buffered, receivedAt);
  }

  /**
   * Factory for a reading whose location was resolved from a cell tower (no GPS).
   */
  public static TelemetryData withApproximateLocation(UUID deviceId, String reason,
      Coordinates coordinates, Double accuracyMeters, BatteryLevel battery, Double temperatureC,
      HealthMetrics health, CellTower cellTower, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, false,
        Objects.requireNonNull(coordinates, "coordinates"),
        accuracyMeters, true, battery, temperatureC, health, cellTower, null, false, receivedAt);
  }

  /**
   * Factory for a reading with no location (no GPS fix and no/failed cell lookup).
   */
  public static TelemetryData withoutLocation(UUID deviceId, String reason, BatteryLevel battery,
      Double temperatureC, HealthMetrics health, CellTower cellTower, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, false,
        null, null, false, battery, temperatureC, health, cellTower, null, false, receivedAt);
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

  public boolean buffered() {
    return buffered;
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

  public HealthMetrics health() {
    return health;
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
