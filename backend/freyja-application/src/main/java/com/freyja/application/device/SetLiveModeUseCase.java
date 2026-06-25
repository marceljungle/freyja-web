package com.freyja.application.device;

import java.time.Instant;

import com.freyja.application.common.AbstractUseCase;
import com.freyja.domain.exception.MessagingException;
import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.MqttPublisher;
import com.freyja.domain.port.out.TimeProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Toggles live (real-time) mode on a device by publishing {@code live_on} /
 * {@code live_off} to its command topic.
 *
 * <p>Two modes:
 * <ul>
 *   <li><b>Bounded</b> (default): a single window; the firmware auto-offs after
 *       FREYJA_LIVE_MAX_DURATION_SEC. The backend mirrors that as a deadline.</li>
 *   <li><b>Persistent</b>: stays on until turned off — a scheduled keep-alive
 *       ({@link LiveModeRefresher}) re-sends {@code live_on} to refresh the
 *       firmware deadline. The firmware low-battery cutoff is the safety net.</li>
 * </ul>
 */
@Service
public class SetLiveModeUseCase extends AbstractUseCase<SetLiveModeCommand, DeviceView> {

  private final DeviceAccessService deviceAccess;

  private final DeviceRepository deviceRepository;

  private final MqttPublisher mqttPublisher;

  private final TimeProvider time;

  /** Bounded-window length, mirroring the firmware FREYJA_LIVE_MAX_DURATION_SEC. */
  private final long windowSeconds;

  public SetLiveModeUseCase(DeviceAccessService deviceAccess,
      DeviceRepository deviceRepository,
      MqttPublisher mqttPublisher,
      TimeProvider time,
      @Value("${freyja.live-mode.window-sec:600}") long windowSeconds) {
    this.deviceAccess = deviceAccess;
    this.deviceRepository = deviceRepository;
    this.mqttPublisher = mqttPublisher;
    this.time = time;
    this.windowSeconds = windowSeconds;
  }

  @Override
  protected DeviceView handle(SetLiveModeCommand input) {
    Device device = deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId());
    Instant now = time.now();

    String payload;
    if (input.enabled()) {
      payload = LiveModeCommands.liveOn(input.interval());
      device.enableLiveMode(input.persistent(), now.plusSeconds(windowSeconds), input.interval(), now);
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
