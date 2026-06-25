package com.freyja.application.device;

import java.time.Instant;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.domain.exception.MessagingException;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.MqttPublisher;
import com.freyja.domain.port.out.TimeProvider;
import org.springframework.stereotype.Service;

/**
 * Toggles live (real-time) mode on a device by publishing {@code live_on} /
 * {@code live_off} to its command topic.
 *
 * <p>Live mode is persistent: it stays on until explicitly turned off. The
 * firmware auto-offs after a short window as a battery safeguard, so a scheduled
 * keep-alive ({@link LiveModeRefresher}) re-sends {@code live_on} to enabled
 * devices to keep them streaming. The firmware's low-battery cutoff remains the
 * hardware safety net.
 *
 * <p>Replaces the old request-location command: the device already reports on
 * motion, so an on-demand single fix made little sense.
 */
@Service
public class SetLiveModeUseCase extends AbstractUseCase<SetLiveModeCommand, DeviceView> {

  private final DeviceAccessService deviceAccess;

  private final DeviceRepository deviceRepository;

  private final MqttPublisher mqttPublisher;

  private final TimeProvider time;

  public SetLiveModeUseCase(DeviceAccessService deviceAccess,
      DeviceRepository deviceRepository,
      MqttPublisher mqttPublisher,
      TimeProvider time) {
    this.deviceAccess = deviceAccess;
    this.deviceRepository = deviceRepository;
    this.mqttPublisher = mqttPublisher;
    this.time = time;
  }

  @Override
  protected DeviceView handle(SetLiveModeCommand input) {
    Device device = deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId());
    Instant now = time.now();

    String payload;
    if (input.enabled()) {
      payload = LiveModeCommands.liveOn(input.interval());
      device.enableLiveMode(input.interval(), now);
    } else {
      payload = LiveModeCommands.LIVE_OFF;
      device.disableLiveMode(now);
    }

    try {
      mqttPublisher.publishToDevice(device.imei(), payload);
    } catch (MessagingException ex) {
      throw ex; // transaction rolls back the live-mode state change
    } catch (RuntimeException ex) {
      throw new MessagingException("Failed to publish live mode command", ex);
    }

    return DeviceView.from(deviceRepository.save(device));
  }
}
