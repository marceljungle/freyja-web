package com.freyja.infrastructure.rest.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Body for {@code POST /api/devices/{id}/live-mode}.
 *
 * @param enabled  true streams live telemetry (live_on), false stops it (live_off).
 * @param interval optional seconds between live updates (null = firmware default).
 */
public record LiveModeRequest(@NotNull Boolean enabled, Integer interval) {
}
