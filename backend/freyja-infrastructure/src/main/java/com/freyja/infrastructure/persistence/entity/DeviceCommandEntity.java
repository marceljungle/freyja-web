package com.freyja.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import com.freyja.domain.model.command.CommandStatus;
import com.freyja.domain.model.command.CommandType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "device_commands")
public class DeviceCommandEntity {

  @Id
  private UUID id;

  @Column(name = "device_id", nullable = false)
  private UUID deviceId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CommandType type;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CommandStatus status;

  @Column(name = "payload")
  private String payload;

  @Column(name = "issued_by")
  private UUID issuedBy;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "sent_at")
  private Instant sentAt;

  @Column(name = "acknowledged_at")
  private Instant acknowledgedAt;

  public DeviceCommandEntity() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  public CommandType getType() {
    return type;
  }

  public void setType(CommandType type) {
    this.type = type;
  }

  public CommandStatus getStatus() {
    return status;
  }

  public void setStatus(CommandStatus status) {
    this.status = status;
  }

  public String getPayload() {
    return payload;
  }

  public void setPayload(String payload) {
    this.payload = payload;
  }

  public UUID getIssuedBy() {
    return issuedBy;
  }

  public void setIssuedBy(UUID issuedBy) {
    this.issuedBy = issuedBy;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getSentAt() {
    return sentAt;
  }

  public void setSentAt(Instant sentAt) {
    this.sentAt = sentAt;
  }

  public Instant getAcknowledgedAt() {
    return acknowledgedAt;
  }

  public void setAcknowledgedAt(Instant acknowledgedAt) {
    this.acknowledgedAt = acknowledgedAt;
  }
}
