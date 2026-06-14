package com.freyja.infrastructure.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;
import org.springframework.stereotype.Component;

/**
 * Surfaces Spring Integration MQTT lifecycle events in the application log so a
 * misconfigured broker URL or a failed subscription is immediately visible
 * (otherwise telemetry simply never arrives, with no hint why).
 */
@Component
public class MqttEventLogger {

  private static final Logger log = LoggerFactory.getLogger(MqttEventLogger.class);

  @EventListener
  public void onSubscribed(MqttSubscribedEvent event) {
    log.info("MQTT {}", event.getMessage());
  }

  @EventListener
  public void onConnectionFailed(MqttConnectionFailedEvent event) {
    Throwable cause = event.getCause();
    log.error("MQTT connection failed: {} (will retry)",
        cause != null ? cause.getMessage() : "unknown cause", cause);
  }
}
