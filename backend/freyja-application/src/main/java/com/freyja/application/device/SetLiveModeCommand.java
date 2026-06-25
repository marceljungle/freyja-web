package com.freyja.application.device;

import java.util.UUID;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

/**
 * Input for {@link SetLiveModeUseCase}: toggle real-time streaming on a device.
 *
 * @param enabled    true sends {@code live_on}, false sends {@code live_off}.
 * @param persistent when enabled, true keeps it running until stopped (keep-alive);
 *                   false is a single firmware-bounded window.
 * @param interval   optional seconds between live updates (null = firmware default).
 */
public record SetLiveModeCommand(UUID ownerId, UUID deviceId, boolean enabled, boolean persistent,
    Integer interval) implements UseCaseInput {

  private static final int MAX_INTERVAL_SEC = 3600;

  @Override
  public void validate() {
    if (ownerId == null) {
      throw new ValidationException("Owner is required");
    }
    if (deviceId == null) {
      throw new ValidationException("Device id is required");
    }
    if (interval != null && (interval < 0 || interval > MAX_INTERVAL_SEC)) {
      throw new ValidationException("Live interval must be between 0 and " + MAX_INTERVAL_SEC + " seconds");
    }
  }
}
