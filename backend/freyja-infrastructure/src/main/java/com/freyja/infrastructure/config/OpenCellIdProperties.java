package com.freyja.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** Binds {@code freyja.opencellid.*} for the cell-tower location fallback. */
@ConfigurationProperties(prefix = "freyja.opencellid")
public class OpenCellIdProperties {

  /** API key. When blank, cell-tower resolution is disabled (returns no location). */
  private String apiKey = "";

  /** Base URL of the OpenCelliD-compatible API. */
  private String baseUrl = "https://opencellid.org";

  /** Connect/read timeout in milliseconds for the lookup call. */
  private int timeoutMs = 3000;

  /** Accuracy (metres) recorded when the provider does not return a range. */
  private double defaultAccuracyMeters = 1000.0;

  public String getApiKey() {
    return apiKey;
  }

  public void setApiKey(String apiKey) {
    this.apiKey = apiKey;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public int getTimeoutMs() {
    return timeoutMs;
  }

  public void setTimeoutMs(int timeoutMs) {
    this.timeoutMs = timeoutMs;
  }

  public double getDefaultAccuracyMeters() {
    return defaultAccuracyMeters;
  }

  public void setDefaultAccuracyMeters(double defaultAccuracyMeters) {
    this.defaultAccuracyMeters = defaultAccuracyMeters;
  }

  public boolean isConfigured() {
    return apiKey != null && !apiKey.isBlank();
  }
}
