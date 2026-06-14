package com.freyja.domain.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.telemetry.TelemetryData;

public interface TelemetryRepository {

  TelemetryData save(TelemetryData telemetry);

  /**
   * Most recent reading for a device, if any.
   */
  Optional<TelemetryData> findLatestByDeviceId(UUID deviceId);

  /**
   * Readings for a device within [from, to] (by server receive time), newest first, capped at {@code limit} rows.
   */
  List<TelemetryData> findByDeviceIdBetween(UUID deviceId, Instant from, Instant to, int limit);
}
