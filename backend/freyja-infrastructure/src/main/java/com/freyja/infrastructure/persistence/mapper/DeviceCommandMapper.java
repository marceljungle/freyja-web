package com.freyja.infrastructure.persistence.mapper;

import com.freyja.domain.model.command.DeviceCommand;
import com.freyja.infrastructure.persistence.entity.DeviceCommandEntity;

public final class DeviceCommandMapper {

  private DeviceCommandMapper() {
  }

  public static DeviceCommand toDomain(DeviceCommandEntity e) {
    return new DeviceCommand(
        e.getId(),
        e.getDeviceId(),
        e.getType(),
        e.getStatus(),
        e.getPayload(),
        e.getIssuedBy(),
        e.getCreatedAt(),
        e.getSentAt(),
        e.getAcknowledgedAt());
  }

  public static DeviceCommandEntity toEntity(DeviceCommand command) {
    DeviceCommandEntity e = new DeviceCommandEntity();
    e.setId(command.id());
    e.setDeviceId(command.deviceId());
    e.setType(command.type());
    e.setStatus(command.status());
    e.setPayload(command.payload());
    e.setIssuedBy(command.issuedBy());
    e.setCreatedAt(command.createdAt());
    e.setSentAt(command.sentAt());
    e.setAcknowledgedAt(command.acknowledgedAt());
    return e;
  }
}
