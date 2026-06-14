package com.freyja.domain.model.telemetry;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.Coordinates;

public class TelemetryData {

  private final Long id;

  private final UUID deviceId;

  private final String reason;

  private final boolean hasFix;

  private final Coordinates coordinates;   // null when hasFix == false

  private final Double accuracy;           // metres, null when hasFix == false

  private final BatteryLevel battery;      // null if not reported

  private final Instant deviceTime;        // GNSS UTC, null when hasFix == false

  private final Instant receivedAt;

  public TelemetryData(Long id, UUID deviceId, String reason, boolean hasFix,
      Coordinates coordinates, Double accuracy, BatteryLevel battery,
      Instant deviceTime, Instant receivedAt) {
    this.id = id;
    this.deviceId = Objects.requireNonNull(deviceId, "deviceId");
    this.reason = reason;
    this.hasFix = hasFix;
    this.coordinates = coordinates;
    this.accuracy = accuracy;
    this.battery = battery;
    this.deviceTime = deviceTime;
    this.receivedAt = Objects.requireNonNull(receivedAt, "receivedAt");
  }

  /**
   * Factory for a reading that carries a valid GNSS fix.
   */
  public static TelemetryData withFix(UUID deviceId, String reason, Coordinates coordinates,
      Double accuracy, BatteryLevel battery,
      Instant deviceTime, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, true,
        Objects.requireNonNull(coordinates, "coordinates"),
        accuracy, battery, deviceTime, receivedAt);
  }

  /**
   * Factory for a reading with no fix (movement reported, location unknown).
   */
  public static TelemetryData withoutFix(UUID deviceId, String reason,
      BatteryLevel battery, Instant receivedAt) {
    return new TelemetryData(null, deviceId, reason, false,
        null, null, battery, null, receivedAt);
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

  public Optional<Coordinates> coordinates() {
    return Optional.ofNullable(coordinates);
  }

  public Optional<Double> accuracy() {
    return Optional.ofNullable(accuracy);
  }

  public Optional<BatteryLevel> battery() {
    return Optional.ofNullable(battery);
  }

  public Optional<Instant> deviceTime() {
    return Optional.ofNullable(deviceTime);
  }

  public Instant receivedAt() {
    return receivedAt;
  }
}
