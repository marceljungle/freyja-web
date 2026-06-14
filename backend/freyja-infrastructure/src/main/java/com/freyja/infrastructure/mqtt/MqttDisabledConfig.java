package com.freyja.infrastructure.mqtt;

import com.freyja.domain.exception.MessagingException;
import com.freyja.domain.port.out.MqttPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Fallback wiring when {@code freyja.mqtt.enabled=false} (e.g. tests or an MQTT-less deployment). Telemetry ingestion is inactive and
 * command publishing fails fast.
 */
@Configuration
@ConditionalOnProperty(prefix = "freyja.mqtt", name = "enabled", havingValue = "false")
public class MqttDisabledConfig {

  private static final Logger log = LoggerFactory.getLogger(MqttDisabledConfig.class);

  @Bean
  public MqttPublisher noOpMqttPublisher() {
    log.warn("MQTT is disabled (freyja.mqtt.enabled=false): telemetry ingestion and "
        + "command publishing are inactive");
    return (imei, payload) -> {
      throw new MessagingException("MQTT is disabled; cannot publish command to device");
    };
  }
}
