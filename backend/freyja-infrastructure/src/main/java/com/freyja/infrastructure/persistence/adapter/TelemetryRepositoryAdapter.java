package com.freyja.infrastructure.persistence.adapter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.port.out.TelemetryRepository;
import com.freyja.infrastructure.persistence.mapper.TelemetryMapper;
import com.freyja.infrastructure.persistence.repository.SpringDataTelemetryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class TelemetryRepositoryAdapter implements TelemetryRepository {

  private final SpringDataTelemetryRepository repository;

  public TelemetryRepositoryAdapter(SpringDataTelemetryRepository repository) {
    this.repository = repository;
  }

  @Override
  public TelemetryData save(TelemetryData telemetry) {
    return TelemetryMapper.toDomain(repository.save(TelemetryMapper.toEntity(telemetry)));
  }

  @Override
  public Optional<TelemetryData> findLatestByDeviceId(UUID deviceId) {
    return repository.findFirstByDeviceIdOrderByReceivedAtDesc(deviceId)
        .map(TelemetryMapper::toDomain);
  }

  @Override
  public List<TelemetryData> findByDeviceIdBetween(UUID deviceId, Instant from, Instant to, int limit) {
    return repository.findByDeviceIdAndReceivedAtBetweenOrderByReceivedAtDesc(
            deviceId, from, to, PageRequest.of(0, limit)).stream()
        .map(TelemetryMapper::toDomain)
        .toList();
  }
}
