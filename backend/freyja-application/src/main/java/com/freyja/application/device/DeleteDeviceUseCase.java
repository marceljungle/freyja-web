package com.freyja.application.device;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceRepository;
import org.springframework.stereotype.Service;

/**
 * Deletes a device owned by the requesting user. Telemetry and queued commands
 * are removed by the database's ON DELETE CASCADE constraints.
 */
@Service
public class DeleteDeviceUseCase extends AbstractUseCase<DeleteDeviceCommand, Void> {

  private final DeviceAccessService deviceAccess;

  private final DeviceRepository deviceRepository;

  public DeleteDeviceUseCase(DeviceAccessService deviceAccess, DeviceRepository deviceRepository) {
    this.deviceAccess = deviceAccess;
    this.deviceRepository = deviceRepository;
  }

  @Override
  protected Void handle(DeleteDeviceCommand input) {
    Device device = deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId());
    deviceRepository.deleteById(device.id());
    return null;
  }
}
