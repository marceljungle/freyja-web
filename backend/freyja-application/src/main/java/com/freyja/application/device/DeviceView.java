package com.freyja.application.device;

import java.time.Instant;
import java.util.UUID;

import com.freyja.domain.model.device.Device;
import com.freyja.domain.vo.NetworkConfig;

public record DeviceView(
    UUID id,
    String imei,
    String name,
    String fwVersion,
    String apn,
    String brokerIp,
    Integer brokerPort,
    Instant lastSeenAt,
    Instant liveModeUntil,
    Instant createdAt) {

  public static DeviceView from(Device device) {
    NetworkConfig cfg = device.networkConfig().orElse(null);
    return new DeviceView(
        device.id(),
        device.imei().value(),
        device.name(),
        device.fwVersion(),
        cfg != null ? cfg.apn() : null,
        cfg != null ? cfg.brokerIp() : null,
        cfg != null ? cfg.brokerPort() : null,
        device.lastSeenAt(),
        device.liveModeUntil().orElse(null),
        device.createdAt());
  }
}
