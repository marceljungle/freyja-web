package com.freyja.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.vo.Imei;
import com.freyja.infrastructure.persistence.mapper.DeviceMapper;
import com.freyja.infrastructure.persistence.repository.SpringDataDeviceRepository;
import org.springframework.stereotype.Repository;

@Repository
public class DeviceRepositoryAdapter implements DeviceRepository {

  private final SpringDataDeviceRepository repository;

  public DeviceRepositoryAdapter(SpringDataDeviceRepository repository) {
    this.repository = repository;
  }

  @Override
  public Device save(Device device) {
    return DeviceMapper.toDomain(repository.save(DeviceMapper.toEntity(device)));
  }

  @Override
  public Optional<Device> findById(UUID id) {
    return repository.findById(id).map(DeviceMapper::toDomain);
  }

  @Override
  public Optional<Device> findByImei(Imei imei) {
    return repository.findByImei(imei.value()).map(DeviceMapper::toDomain);
  }

  @Override
  public List<Device> findByOwnerId(UUID ownerId) {
    return repository.findByOwnerIdOrderByCreatedAtAsc(ownerId).stream()
        .map(DeviceMapper::toDomain)
        .toList();
  }

  @Override
  public List<Device> findByLiveModeEnabled() {
    return repository.findByLiveModeEnabledTrue().stream()
        .map(DeviceMapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsByImei(Imei imei) {
    return repository.existsByImei(imei.value());
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
