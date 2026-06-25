package com.freyja.infrastructure.persistence.mapper;

import com.freyja.domain.model.device.Device;
import com.freyja.domain.vo.Imei;
import com.freyja.domain.vo.NetworkConfig;
import com.freyja.infrastructure.persistence.entity.DeviceEntity;

public final class DeviceMapper {

  private DeviceMapper() {
  }

  public static Device toDomain(DeviceEntity e) {
    NetworkConfig networkConfig = null;
    if (e.getApn() != null && e.getBrokerIp() != null && e.getBrokerPort() != null) {
      networkConfig = NetworkConfig.of(e.getApn(), e.getBrokerIp(), e.getBrokerPort());
    }
    return new Device(
        e.getId(),
        Imei.of(e.getImei()),
        e.getName(),
        e.getFwVersion(),
        e.getOwnerId(),
        networkConfig,
        e.getLastSeenAt(),
        e.isLiveModeEnabled(),
        e.getLiveModeInterval(),
        e.getCreatedAt(),
        e.getUpdatedAt());
  }

  public static DeviceEntity toEntity(Device device) {
    DeviceEntity e = new DeviceEntity();
    e.setId(device.id());
    e.setImei(device.imei().value());
    e.setName(device.name());
    e.setFwVersion(device.fwVersion());
    e.setOwnerId(device.ownerId());
    device.networkConfig().ifPresentOrElse(cfg -> {
      e.setApn(cfg.apn());
      e.setBrokerIp(cfg.brokerIp());
      e.setBrokerPort(cfg.brokerPort());
    }, () -> {
      e.setApn(null);
      e.setBrokerIp(null);
      e.setBrokerPort(null);
    });
    e.setLastSeenAt(device.lastSeenAt());
    e.setLiveModeEnabled(device.liveModeEnabled());
    e.setLiveModeInterval(device.liveModeInterval().orElse(null));
    e.setCreatedAt(device.createdAt());
    e.setUpdatedAt(device.updatedAt());
    return e;
  }
}
