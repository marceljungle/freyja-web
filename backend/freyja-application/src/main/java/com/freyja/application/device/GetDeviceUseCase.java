package com.freyja.application.device;

import com.freyja.application.common.AbstractReadOnlyUseCase;
import org.springframework.stereotype.Service;

@Service
public class GetDeviceUseCase extends AbstractReadOnlyUseCase<GetDeviceQuery, DeviceView> {

  private final DeviceAccessService deviceAccess;

  public GetDeviceUseCase(DeviceAccessService deviceAccess) {
    this.deviceAccess = deviceAccess;
  }

  @Override
  protected DeviceView handle(GetDeviceQuery input) {
    return DeviceView.from(deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId()));
  }
}
