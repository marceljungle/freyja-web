package com.freyja.application.device;

/** Builds the downlink JSON commands for the firmware live-mode protocol. */
final class LiveModeCommands {

  static final String LIVE_OFF = "{\"cmd\":\"live_off\"}";

  private LiveModeCommands() {
  }

  static String liveOn(Integer interval) {
    if (interval == null) {
      return "{\"cmd\":\"live_on\"}";
    }
    return "{\"cmd\":\"live_on\",\"interval\":" + interval + "}";
  }
}
