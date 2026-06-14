package com.freyja.application.device;

import java.util.UUID;

import com.freyja.domain.exception.EntityNotFoundException;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceRepository;
import org.springframework.stereotype.Service;

/**
 * Loads a device while enforcing that it belongs to the requesting user. A not-found and a not-owned device are reported identically (404)
 * so ownership cannot be probed.
 */
@Service
public class DeviceAccessService {

  private final DeviceRepository deviceRepository;

  public DeviceAccessService(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  public Device requireOwnedDevice(UUID ownerId, UUID deviceId) {
    Device device = deviceRepository.findById(deviceId)
        .orElseThrow(() -> new EntityNotFoundException("Device not found"));
    if (!device.isOwnedBy(ownerId)) {
      throw new EntityNotFoundException("Device not found");
    }
    return device;
  }
}
