package com.freyja.application.command;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.application.device.DeviceAccessService;
import com.freyja.domain.exception.MessagingException;
import com.freyja.domain.model.command.CommandType;
import com.freyja.domain.model.command.DeviceCommand;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceCommandRepository;
import com.freyja.domain.port.out.MqttPublisher;
import com.freyja.domain.port.out.TimeProvider;
import org.springframework.stereotype.Service;

@Service
public class RequestDeviceLocationUseCase
    extends AbstractUseCase<RequestLocationCommand, CommandView> {

  static final String REQUEST_LOCATION_PAYLOAD = "{\"cmd\":\"request_location\"}"; //TODO maybe we should have an enum class with all the commands

  private final DeviceAccessService deviceAccess;

  private final DeviceCommandRepository commandRepository;

  private final MqttPublisher mqttPublisher;

  private final TimeProvider time;

  public RequestDeviceLocationUseCase(DeviceAccessService deviceAccess,
      DeviceCommandRepository commandRepository,
      MqttPublisher mqttPublisher,
      TimeProvider time) {
    this.deviceAccess = deviceAccess;
    this.commandRepository = commandRepository;
    this.mqttPublisher = mqttPublisher;
    this.time = time;
  }

  @Override
  protected CommandView handle(RequestLocationCommand input) {
    Device device = deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId());

    DeviceCommand command = DeviceCommand.create(device.id(), CommandType.REQUEST_LOCATION,
        REQUEST_LOCATION_PAYLOAD, input.ownerId(), time.now());
    command = commandRepository.save(command);

    try {
      mqttPublisher.publishToDevice(device.imei(), REQUEST_LOCATION_PAYLOAD);
    } catch (MessagingException ex) {
      throw ex; // transaction rolls back the persisted command
    } catch (RuntimeException ex) {
      throw new MessagingException("Failed to queue command on the broker", ex);
    }

    command.markSent(time.now());
    return CommandView.from(commandRepository.save(command));
  }
}
