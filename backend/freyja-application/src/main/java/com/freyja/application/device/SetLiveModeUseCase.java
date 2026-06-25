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
 * {@code live_off} to its command topic, and records the expected on-until
 * deadline (mirroring the firmware auto-off) so the UI shows an accurate toggle.
 *
 * <p>Replaces the old request-location command: the device already reports on
 * motion, so an on-demand single fix made little sense; live mode streams fixes
 * for a bounded window instead.
 */
@Service
public class SetLiveModeUseCase extends AbstractUseCase<SetLiveModeCommand, DeviceView> {

  private static final String LIVE_OFF_PAYLOAD = "{\"cmd\":\"live_off\"}";

  private final DeviceAccessService deviceAccess;

  private final DeviceRepository deviceRepository;

  private final MqttPublisher mqttPublisher;

  private final TimeProvider time;

  /** Mirrors the firmware FREYJA_LIVE_MAX_DURATION_SEC auto-off (default 600 s). */
  private final long maxDurationSeconds;

  public SetLiveModeUseCase(DeviceAccessService deviceAccess,
      DeviceRepository deviceRepository,
      MqttPublisher mqttPublisher,
      TimeProvider time,
      @Value("${freyja.live-mode.max-duration-sec:600}") long maxDurationSeconds) {
    this.deviceAccess = deviceAccess;
    this.deviceRepository = deviceRepository;
    this.mqttPublisher = mqttPublisher;
    this.time = time;
    this.maxDurationSeconds = maxDurationSeconds;
  }

  @Override
  protected DeviceView handle(SetLiveModeCommand input) {
    Device device = deviceAccess.requireOwnedDevice(input.ownerId(), input.deviceId());
    Instant now = time.now();

    String payload;
    if (input.enabled()) {
      payload = liveOnPayload(input.interval());
      device.enableLiveMode(now.plusSeconds(maxDurationSeconds), now);
    } else {
      payload = LIVE_OFF_PAYLOAD;
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

  private static String liveOnPayload(Integer interval) {
    if (interval == null) {
      return "{\"cmd\":\"live_on\"}";
    }
    return "{\"cmd\":\"live_on\",\"interval\":" + interval + "}";
  }
}
