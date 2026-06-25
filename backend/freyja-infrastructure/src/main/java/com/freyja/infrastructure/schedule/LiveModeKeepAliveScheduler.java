package com.freyja.infrastructure.schedule;

import com.freyja.application.device.LiveModeRefresher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Re-publishes {@code live_on} to devices with live mode enabled, keeping them
 * streaming past the firmware's short auto-off window. The interval must be
 * shorter than FREYJA_LIVE_MAX_DURATION_SEC on the device (600 s); 5 min default.
 */
@Component
public class LiveModeKeepAliveScheduler {

  private final LiveModeRefresher refresher;

  public LiveModeKeepAliveScheduler(LiveModeRefresher refresher) {
    this.refresher = refresher;
  }

  @Scheduled(fixedDelayString = "${freyja.live-mode.keepalive-ms:300000}",
      initialDelayString = "${freyja.live-mode.keepalive-ms:300000}")
  public void keepAlive() {
    refresher.refreshActiveSessions();
  }
}
