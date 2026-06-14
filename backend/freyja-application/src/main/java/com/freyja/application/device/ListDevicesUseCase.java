package com.freyja.application.device;

import java.util.List;

import com.freyja.application.common.AbstractReadOnlyUseCase;
import com.freyja.domain.port.out.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class ListDevicesUseCase extends AbstractReadOnlyUseCase<ListDevicesQuery, List<DeviceView>> {

  private final DeviceRepository deviceRepository;

  public ListDevicesUseCase(DeviceRepository deviceRepository) {
    this.deviceRepository = deviceRepository;
  }

  @Override
  protected List<DeviceView> handle(ListDevicesQuery input) {
    return deviceRepository.findByOwnerId(input.ownerId()).stream()
        .map(DeviceView::from)
        .toList();
  }
}
