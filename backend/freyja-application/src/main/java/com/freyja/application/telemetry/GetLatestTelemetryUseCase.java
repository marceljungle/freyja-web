package com.freyja.application.telemetry;

import java.util.Optional;

import com.freyja.application.common.AbstractReadOnlyUseCase;
import com.freyja.application.device.DeviceAccessService;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.TelemetryRepository;
import org.springframework.stereotype.Service;

@Service
public class GetLatestTelemetryUseCase
    extends AbstractReadOnlyUseCase<GetLatestTelemetryQuery, Optional<TelemetryView>> {

  private final DeviceAccessService deviceAccess;

  private final TelemetryRepository telemetryRepository;

  public GetLatestTelemetryUseCase(DeviceAccessService deviceAccess,
      TelemetryRepository telemetryRepository) {
    this.deviceAccess = deviceAccess;
    this.telemetryRepository = telemetryRepository;
  }

  @Override
  protected Optional<TelemetryView> handle(GetLatestTelemetryQuery input) {
    Device device = deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId());
    return telemetryRepository.findLatestByDeviceId(device.id()).map(TelemetryView::from);
  }
}
