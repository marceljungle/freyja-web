package com.freyja.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.device.Device;
import com.freyja.domain.vo.Imei;

public interface DeviceRepository {

  Device save(Device device);

  Optional<Device> findById(UUID id);

  Optional<Device> findByImei(Imei imei);

  List<Device> findByOwnerId(UUID ownerId);

  /** Devices with persistent live mode enabled (for the keep-alive). */
  List<Device> findByLivePersistent();

  boolean existsByImei(Imei imei);

  /** Deletes a device; related telemetry and commands cascade at the database level. */
  void deleteById(UUID id);
}
