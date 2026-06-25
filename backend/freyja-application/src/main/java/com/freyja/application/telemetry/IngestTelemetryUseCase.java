package com.freyja.application.telemetry;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.domain.exception.ValidationException;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.port.out.CellLocationResolver;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.TelemetryRepository;
import com.freyja.domain.port.out.TimeProvider;
import com.freyja.domain.vo.BatteryLevel;
import com.freyja.domain.vo.CellLocation;
import com.freyja.domain.vo.CellTower;
import com.freyja.domain.vo.Coordinates;
import com.freyja.domain.vo.HealthMetrics;
import com.freyja.domain.vo.Imei;
import org.springframework.stereotype.Service;

@Service
public class IngestTelemetryUseCase
    extends AbstractUseCase<IngestTelemetryCommand, Optional<TelemetryView>> {

  private final DeviceRepository deviceRepository;

  private final TelemetryRepository telemetryRepository;

  private final CellLocationResolver cellLocationResolver;

  private final TimeProvider time;

  public IngestTelemetryUseCase(DeviceRepository deviceRepository,
      TelemetryRepository telemetryRepository,
      CellLocationResolver cellLocationResolver,
      TimeProvider time) {
    this.deviceRepository = deviceRepository;
    this.telemetryRepository = telemetryRepository;
    this.cellLocationResolver = cellLocationResolver;
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
    Double temperatureC = input.temperatureC();
    HealthMetrics health = HealthMetrics.of(input.rsrp(), input.trackedSvs(), input.svsUsed(), input.cn0());

    TelemetryData reading = buildReading(device.id(), input, battery, temperatureC, health, now);
    TelemetryData saved = telemetryRepository.save(reading);

    device.markSeen(null, now);
    deviceRepository.save(device);

    return Optional.of(TelemetryView.from(saved));
  }

  private TelemetryData buildReading(UUID deviceId, IngestTelemetryCommand input,
      BatteryLevel battery, Double temperatureC, HealthMetrics health, Instant now) {
    if (input.hasFix()) {
      Coordinates coordinates = Coordinates.of(input.latitude(), input.longitude());
      return TelemetryData.withFix(deviceId, input.reason(), coordinates,
          input.accuracy(), battery, temperatureC, health, input.deviceTime(), input.buffered(), now);
    }

    // No GPS fix: try a cell-tower location fallback when the cell is known.
    if (input.hasCellTower()) {
      CellTower tower = CellTower.of(input.mcc(), input.mnc(), input.tac(), input.cellId());
      Optional<CellLocation> resolved = cellLocationResolver.resolve(tower);
      if (resolved.isPresent()) {
        CellLocation location = resolved.get();
        return TelemetryData.withApproximateLocation(deviceId, input.reason(),
            location.coordinates(), location.accuracyMeters(), battery, temperatureC, health, tower, now);
      }
      return TelemetryData.withoutLocation(deviceId, input.reason(), battery, temperatureC, health, tower, now);
    }

    return TelemetryData.withoutLocation(deviceId, input.reason(), battery, temperatureC, health, null, now);
  }
}
