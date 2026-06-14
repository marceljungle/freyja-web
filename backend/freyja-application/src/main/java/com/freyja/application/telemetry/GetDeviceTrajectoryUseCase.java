package com.freyja.application.telemetry;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import com.freyja.application.common.AbstractReadOnlyUseCase;
import com.freyja.application.device.DeviceAccessService;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.TelemetryRepository;
import com.freyja.domain.port.out.TimeProvider;
import org.springframework.stereotype.Service;

@Service
public class GetDeviceTrajectoryUseCase
    extends AbstractReadOnlyUseCase<GetTrajectoryQuery, List<TelemetryView>> {

  private static final Duration DEFAULT_WINDOW = Duration.ofHours(24);

  private final DeviceAccessService deviceAccess;

  private final TelemetryRepository telemetryRepository;

  private final TimeProvider time;

  public GetDeviceTrajectoryUseCase(DeviceAccessService deviceAccess,
      TelemetryRepository telemetryRepository,
      TimeProvider time) {
    this.deviceAccess = deviceAccess;
    this.telemetryRepository = telemetryRepository;
    this.time = time;
  }

  @Override
  protected List<TelemetryView> handle(GetTrajectoryQuery input) {
    Device device = deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId());

    Instant to = input.to() != null ? input.to() : time.now();
    Instant from = input.from() != null ? input.from() : to.minus(DEFAULT_WINDOW);
    int limit = input.limit() != null
        ? Math.min(input.limit(), GetTrajectoryQuery.MAX_LIMIT)
        : GetTrajectoryQuery.DEFAULT_LIMIT;

    return telemetryRepository.findByDeviceIdBetween(device.id(), from, to, limit).stream()
        .map(TelemetryView::from)
        .toList();
  }
}
