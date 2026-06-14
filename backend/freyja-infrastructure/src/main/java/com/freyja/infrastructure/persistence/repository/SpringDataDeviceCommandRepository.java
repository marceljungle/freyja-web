package com.freyja.infrastructure.persistence.repository;

import java.util.List;
import java.util.UUID;

import com.freyja.infrastructure.persistence.entity.DeviceCommandEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDeviceCommandRepository extends JpaRepository<DeviceCommandEntity, UUID> {

  List<DeviceCommandEntity> findByDeviceIdOrderByCreatedAtDesc(UUID deviceId);
}
