package com.freyja.application.device;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.domain.exception.ConflictException;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.TimeProvider;
import com.freyja.domain.vo.Imei;
import com.freyja.domain.vo.NetworkConfig;
import org.springframework.stereotype.Service;

@Service
public class RegisterDeviceUseCase extends AbstractUseCase<RegisterDeviceCommand, DeviceView> {

  private final DeviceRepository deviceRepository;

  private final TimeProvider time;

  public RegisterDeviceUseCase(DeviceRepository deviceRepository, TimeProvider time) {
    this.deviceRepository = deviceRepository;
    this.time = time;
  }

  @Override
  protected DeviceView handle(RegisterDeviceCommand input) {
    Imei imei = Imei.of(input.imei());
    if (deviceRepository.existsByImei(imei)) {
      throw new ConflictException("A device with this IMEI is already registered");
    }

    NetworkConfig networkConfig = input.hasNetworkConfig()
        ? NetworkConfig.of(input.apn(), input.brokerIp(), input.brokerPort())
        : null;

    Device device = Device.register(imei, input.name(), input.fwVersion(),
        input.ownerId(), networkConfig, time.now());
    return DeviceView.from(deviceRepository.save(device));
  }
}
