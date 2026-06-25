package com.freyja.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "devices")
public class DeviceEntity {

  @Id
  private UUID id;

  @Column(nullable = false)
  private String imei;

  @Column(nullable = false)
  private String name;

  @Column(name = "fw_version")
  private String fwVersion;

  @Column(name = "owner_id", nullable = false)
  private UUID ownerId;

  @Column(name = "apn")
  private String apn;

  @Column(name = "broker_ip")
  private String brokerIp;

  @Column(name = "broker_port")
  private Integer brokerPort;

  @Column(name = "last_seen_at")
  private Instant lastSeenAt;

  @Column(name = "live_mode_until")
  private Instant liveModeUntil;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  public DeviceEntity() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getImei() {
    return imei;
  }

  public void setImei(String imei) {
    this.imei = imei;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFwVersion() {
    return fwVersion;
  }

  public void setFwVersion(String fwVersion) {
    this.fwVersion = fwVersion;
  }

  public UUID getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(UUID ownerId) {
    this.ownerId = ownerId;
  }

  public String getApn() {
    return apn;
  }

  public void setApn(String apn) {
    this.apn = apn;
  }

  public String getBrokerIp() {
    return brokerIp;
  }

  public void setBrokerIp(String brokerIp) {
    this.brokerIp = brokerIp;
  }

  public Integer getBrokerPort() {
    return brokerPort;
  }

  public void setBrokerPort(Integer brokerPort) {
    this.brokerPort = brokerPort;
  }

  public Instant getLastSeenAt() {
    return lastSeenAt;
  }

  public void setLastSeenAt(Instant lastSeenAt) {
    this.lastSeenAt = lastSeenAt;
  }

  public Instant getLiveModeUntil() {
    return liveModeUntil;
  }

  public void setLiveModeUntil(Instant liveModeUntil) {
    this.liveModeUntil = liveModeUntil;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
