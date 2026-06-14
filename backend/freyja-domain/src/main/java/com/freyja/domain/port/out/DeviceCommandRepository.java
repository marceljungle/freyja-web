package com.freyja.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.command.DeviceCommand;

public interface DeviceCommandRepository {

  DeviceCommand save(DeviceCommand command);

  Optional<DeviceCommand> findById(UUID id);

  List<DeviceCommand> findByDeviceId(UUID deviceId);
}
