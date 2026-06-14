package com.freyja.infrastructure.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.infrastructure.persistence.entity.TelemetryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataTelemetryRepository extends JpaRepository<TelemetryEntity, Long> {

  Optional<TelemetryEntity> findFirstByDeviceIdOrderByReceivedAtDesc(UUID deviceId);

  List<TelemetryEntity> findByDeviceIdAndReceivedAtBetweenOrderByReceivedAtDesc(
      UUID deviceId, Instant from, Instant to, Pageable pageable);
}
