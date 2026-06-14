package com.freyja.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.infrastructure.persistence.entity.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDeviceRepository extends JpaRepository<DeviceEntity, UUID> {

  Optional<DeviceEntity> findByImei(String imei);

  boolean existsByImei(String imei);

  List<DeviceEntity> findByOwnerIdOrderByCreatedAtAsc(UUID ownerId);
}
