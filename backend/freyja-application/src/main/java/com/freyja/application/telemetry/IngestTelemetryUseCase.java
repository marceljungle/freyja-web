package com.freyja.application.telemetry;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.domain.exception.ValidationException;
import com.freyja.domain.model.command.CommandStatus;
import com.freyja.domain.model.command.DeviceCommand;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.port.out.DeviceCommandRepository;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.TelemetryRepository;
import com.freyja.domain.port.out.TimeProvider;
import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.Coordinates;
import com.freyja.domain.vo.Imei;
import org.springframework.stereotype.Service;

@Service
public class IngestTelemetryUseCase
    extends AbstractUseCase<IngestTelemetryCommand, Optional<TelemetryView>> {

  private static final String DEFAULT_WAKE_REASON = "motion";

  private final DeviceRepository deviceRepository;

  private final TelemetryRepository telemetryRepository;

  private final DeviceCommandRepository deviceCommandRepository;

  private final TimeProvider time;

  public IngestTelemetryUseCase(DeviceRepository deviceRepository,
      TelemetryRepository telemetryRepository,
      DeviceCommandRepository deviceCommandRepository,
      TimeProvider time) {
    this.deviceRepository = deviceRepository;
    this.telemetryRepository = telemetryRepository;
    this.deviceCommandRepository = deviceCommandRepository;
    this.time = time;
  }

  @Override
  protected Optional<TelemetryView> handle(IngestTelemetryCommand input) {
    Imei imei;
    try {
      imei = Imei.of(input.imei());
    } catch (ValidationException malformed) {
      return Optional.empty(); // unparseable id -> treat as unknown, drop
    }

    Optional<Device> deviceOpt = deviceRepository.findByImei(imei);
    if (deviceOpt.isEmpty()) {
      return Optional.empty(); // unregistered device, drop
    }
    Device device = deviceOpt.get();
    Instant now = time.now();

    BatteryLevel battery = input.batteryMv() != null
        ? BatteryLevel.ofMillivolts(input.batteryMv())
        : null;

    TelemetryData reading;
    if (input.hasFix()) {
      Coordinates coordinates = Coordinates.of(input.latitude(), input.longitude());
      reading = TelemetryData.withFix(device.id(), input.reason(), coordinates,
          input.accuracy(), battery, input.deviceTime(), now);
    } else {
      reading = TelemetryData.withoutFix(device.id(), input.reason(), battery, now);
    }

    TelemetryData saved = telemetryRepository.save(reading);

    device.markSeen(null, now);
    deviceRepository.save(device);

    acknowledgeOutstandingCommand(device.id(), input.reason(), now);

    return Optional.of(TelemetryView.from(saved));
  }

  private void acknowledgeOutstandingCommand(UUID deviceId, String reason, Instant now) {
    if (reason == null || reason.equalsIgnoreCase(DEFAULT_WAKE_REASON)) {
      return; // an ordinary motion wake, not a response to a command
    }
    deviceCommandRepository.findByDeviceId(deviceId).stream()
        .filter(c -> c.status() == CommandStatus.SENT)
        .max(Comparator.comparing(DeviceCommand::createdAt))
        .ifPresent(command -> {
          command.markAcknowledged(now);
          deviceCommandRepository.save(command);
        });
  }
}
