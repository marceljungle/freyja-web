package com.freyja.domain.model.device;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.exception.ValidationException;
import com.freyja.domain.vo.Imei;
import com.freyja.domain.vo.NetworkConfig;

public class Device {

  private static final int NAME_MAX = 120;

  private final UUID id;

  private final Imei imei;

  private final UUID ownerId;

  private final Instant createdAt;

  private String name;

  private String fwVersion;

  private NetworkConfig networkConfig;

  private Instant lastSeenAt;

  private Instant liveModeUntil;

  private Instant updatedAt;

  public Device(UUID id, Imei imei, String name, String fwVersion, UUID ownerId,
      NetworkConfig networkConfig, Instant lastSeenAt, Instant liveModeUntil,
      Instant createdAt, Instant updatedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.imei = Objects.requireNonNull(imei, "imei");
    this.name = normaliseName(name);
    this.fwVersion = fwVersion;
    this.ownerId = Objects.requireNonNull(ownerId, "ownerId");
    this.networkConfig = networkConfig;
    this.lastSeenAt = lastSeenAt;
    this.liveModeUntil = liveModeUntil;
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
  }

  /**
   * Factory for registering a newly provisioned device to an owner.
   */
  public static Device register(Imei imei, String name, String fwVersion, UUID ownerId,
      NetworkConfig networkConfig, Instant now) {
    return new Device(UUID.randomUUID(), imei, name, fwVersion, ownerId,
        networkConfig, null, null, now, now);
  }

  private static String normaliseName(String name) {
    if (name == null || name.isBlank()) {
      throw new ValidationException("Device name must not be blank");
    }
    String trimmed = name.trim();
    if (trimmed.length() > NAME_MAX) {
      throw new ValidationException("Device name must be at most " + NAME_MAX + " characters");
    }
    return trimmed;
  }

  /**
   * Record that telemetry was just received from this device.
   */
  public void markSeen(String fwVersion, Instant seenAt) {
    this.lastSeenAt = seenAt;
    if (fwVersion != null && !fwVersion.isBlank()) {
      this.fwVersion = fwVersion;
    }
    this.updatedAt = seenAt;
  }

  public void rename(String name, Instant now) {
    this.name = normaliseName(name);
    this.updatedAt = now;
  }

  /**
   * Mark live (real-time) mode as active until {@code until}. Mirrors the
   * firmware auto-off deadline so the UI can show an accurate, self-expiring
   * toggle state.
   */
  public void enableLiveMode(Instant until, Instant now) {
    this.liveModeUntil = until;
    this.updatedAt = now;
  }

  public void disableLiveMode(Instant now) {
    this.liveModeUntil = null;
    this.updatedAt = now;
  }

  public UUID id() {
    return id;
  }

  public Imei imei() {
    return imei;
  }

  public String name() {
    return name;
  }

  public String fwVersion() {
    return fwVersion;
  }

  public UUID ownerId() {
    return ownerId;
  }

  public Optional<NetworkConfig> networkConfig() {
    return Optional.ofNullable(networkConfig);
  }

  public Instant lastSeenAt() {
    return lastSeenAt;
  }

  public Optional<Instant> liveModeUntil() {
    return Optional.ofNullable(liveModeUntil);
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant updatedAt() {
    return updatedAt;
  }

  public boolean isOwnedBy(UUID userId) {
    return ownerId.equals(userId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Device other)) {
      return false;
    }
    return id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
