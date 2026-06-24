package com.freyja.domain.vo;

/**
 * Radio and GNSS health diagnostics reported with telemetry (sent even without a
 * GPS fix, so the backend can distinguish "antenna sees sky but too few SVs"
 * from "no signal").
 *
 * @param rsrpDbm    LTE Reference Signal Received Power in dBm (negative; null if unknown).
 * @param trackedSvs GNSS satellites visible to the antenna.
 * @param svsUsed    GNSS satellites actually used in the fix.
 * @param cn0        Peak carrier-to-noise ratio in dB-Hz.
 */
public record HealthMetrics(Integer rsrpDbm, Integer trackedSvs, Integer svsUsed, Double cn0) {

  public static final HealthMetrics EMPTY = new HealthMetrics(null, null, null, null);

  public static HealthMetrics of(Integer rsrpDbm, Integer trackedSvs, Integer svsUsed, Double cn0) {
    return new HealthMetrics(rsrpDbm, trackedSvs, svsUsed, cn0);
  }

  public boolean isPresent() {
    return rsrpDbm != null || trackedSvs != null || svsUsed != null || cn0 != null;
  }
}
