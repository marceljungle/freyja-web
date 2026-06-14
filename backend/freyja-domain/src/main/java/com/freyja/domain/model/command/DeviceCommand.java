package com.freyja.domain.model.command;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class DeviceCommand {

  private final UUID id;

  private final UUID deviceId;

  private final CommandType type;

  private final String payload;     // JSON published to the device

  private final UUID issuedBy;      // user who requested it

  private final Instant createdAt;

  private CommandStatus status;

  private Instant sentAt;

  private Instant acknowledgedAt;

  public DeviceCommand(UUID id, UUID deviceId, CommandType type, CommandStatus status,
      String payload, UUID issuedBy, Instant createdAt,
      Instant sentAt, Instant acknowledgedAt) {
    this.id = Objects.requireNonNull(id, "id");
    this.deviceId = Objects.requireNonNull(deviceId, "deviceId");
    this.type = Objects.requireNonNull(type, "type");
    this.status = Objects.requireNonNull(status, "status");
    this.payload = payload;
    this.issuedBy = issuedBy;
    this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    this.sentAt = sentAt;
    this.acknowledgedAt = acknowledgedAt;
  }

  /**
   * Factory for a freshly queued command (status PENDING).
   */
  public static DeviceCommand create(UUID deviceId, CommandType type, String payload,
      UUID issuedBy, Instant now) {
    return new DeviceCommand(UUID.randomUUID(), deviceId, type, CommandStatus.PENDING,
        payload, issuedBy, now, null, null);
  }

  public void markSent(Instant now) {
    this.status = CommandStatus.SENT;
    this.sentAt = now;
  }

  public void markFailed() {
    this.status = CommandStatus.FAILED;
  }

  public void markAcknowledged(Instant now) {
    this.status = CommandStatus.ACKED;
    this.acknowledgedAt = now;
  }

  public UUID id() {
    return id;
  }

  public UUID deviceId() {
    return deviceId;
  }

  public CommandType type() {
    return type;
  }

  public CommandStatus status() {
    return status;
  }

  public String payload() {
    return payload;
  }

  public UUID issuedBy() {
    return issuedBy;
  }

  public Instant createdAt() {
    return createdAt;
  }

  public Instant sentAt() {
    return sentAt;
  }

  public Instant acknowledgedAt() {
    return acknowledgedAt;
  }
}
