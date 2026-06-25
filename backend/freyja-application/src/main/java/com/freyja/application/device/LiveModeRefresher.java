package com.freyja.application.device;

import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.MqttPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Keeps live (real-time) mode persistent: the firmware auto-offs after a short
 * window as a battery safeguard, so this re-publishes {@code live_on} to every
 * device that has live mode enabled, refreshing the firmware's deadline. Driven
 * by a scheduler in the infrastructure layer (at an interval shorter than the
 * firmware auto-off).
 */
@Service
public class LiveModeRefresher {

  private static final Logger log = LoggerFactory.getLogger(LiveModeRefresher.class);

  private final DeviceRepository deviceRepository;

  private final MqttPublisher mqttPublisher;

  public LiveModeRefresher(DeviceRepository deviceRepository, MqttPublisher mqttPublisher) {
    this.deviceRepository = deviceRepository;
    this.mqttPublisher = mqttPublisher;
  }

  @Transactional(readOnly = true)
  public void refreshActiveSessions() {
    for (Device device : deviceRepository.findByLiveModeEnabled()) {
      try {
        mqttPublisher.publishToDevice(device.imei(), LiveModeCommands.liveOn(device.liveModeInterval().orElse(null)));
      } catch (RuntimeException ex) {
        // One device failing must not stop the others; it retries next tick.
        log.warn("Live-mode keep-alive failed for device {}: {}", device.id(), ex.getMessage());
      }
    }
  }
}
