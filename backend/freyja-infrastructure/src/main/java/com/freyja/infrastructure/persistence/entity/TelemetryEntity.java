package com.freyja.infrastructure.persistence.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "telemetry")
public class TelemetryEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "device_id", nullable = false)
  private UUID deviceId;

  @Column(name = "reason")
  private String reason;

  @Column(name = "has_fix", nullable = false)
  private boolean hasFix;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "accuracy")
  private Double accuracy;

  @Column(name = "battery_mv")
  private Integer batteryMv;

  @Column(name = "device_time")
  private Instant deviceTime;

  @Column(name = "received_at", nullable = false)
  private Instant receivedAt;

  public TelemetryEntity() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(UUID deviceId) {
    this.deviceId = deviceId;
  }

  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  public boolean isHasFix() {
    return hasFix;
  }

  public void setHasFix(boolean hasFix) {
    this.hasFix = hasFix;
  }

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public Double getAccuracy() {
    return accuracy;
  }

  public void setAccuracy(Double accuracy) {
    this.accuracy = accuracy;
  }

  public Integer getBatteryMv() {
    return batteryMv;
  }

  public void setBatteryMv(Integer batteryMv) {
    this.batteryMv = batteryMv;
  }

  public Instant getDeviceTime() {
    return deviceTime;
  }

  public void setDeviceTime(Instant deviceTime) {
    this.deviceTime = deviceTime;
  }

  public Instant getReceivedAt() {
    return receivedAt;
  }

  public void setReceivedAt(Instant receivedAt) {
    this.receivedAt = receivedAt;
  }
}
