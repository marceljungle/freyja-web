package com.freyja.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.command.DeviceCommand;
import com.freyja.domain.port.out.DeviceCommandRepository;
import com.freyja.infrastructure.persistence.mapper.DeviceCommandMapper;
import com.freyja.infrastructure.persistence.repository.SpringDataDeviceCommandRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceCommandRepositoryAdapter implements DeviceCommandRepository {

  private final SpringDataDeviceCommandRepository repository;

  public DeviceCommandRepositoryAdapter(SpringDataDeviceCommandRepository repository) {
    this.repository = repository;
  }

  @Override
  public DeviceCommand save(DeviceCommand command) {
    return DeviceCommandMapper.toDomain(repository.save(DeviceCommandMapper.toEntity(command)));
  }

  @Override
  public Optional<DeviceCommand> findById(UUID id) {
    return repository.findById(id).map(DeviceCommandMapper::toDomain);
  }

  @Override
  public List<DeviceCommand> findByDeviceId(UUID deviceId) {
    return repository.findByDeviceIdOrderByCreatedAtDesc(deviceId).stream()
        .map(DeviceCommandMapper::toDomain)
        .toList();
  }
}
