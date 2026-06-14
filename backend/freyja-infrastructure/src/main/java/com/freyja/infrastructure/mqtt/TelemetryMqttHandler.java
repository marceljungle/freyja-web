package com.freyja.infrastructure.mqtt;

import java.util.Optional;

import com.freyja.application.telemetry.IngestTelemetryCommand;
import com.freyja.application.telemetry.IngestTelemetryUseCase;
import com.freyja.application.telemetry.TelemetryView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

/**
 * Bridges inbound MQTT telemetry messages to the {@link IngestTelemetryUseCase}. Failures are logged and swallowed so a single bad payload
 * never disrupts the MQTT subscription.
 */
@Component
public class TelemetryMqttHandler {

  private static final Logger log = LoggerFactory.getLogger(TelemetryMqttHandler.class);

  private final FirmwareTelemetryParser parser;

  private final IngestTelemetryUseCase ingestTelemetry;

  public TelemetryMqttHandler(FirmwareTelemetryParser parser,
      IngestTelemetryUseCase ingestTelemetry) {
    this.parser = parser;
    this.ingestTelemetry = ingestTelemetry;
  }

  private static String topicOf(MessageHeaders headers) {
    Object topic = headers.get(MqttHeaders.RECEIVED_TOPIC);
    return topic != null ? topic.toString() : "?";
  }

  public void handle(Message<?> message) {
    String topic = topicOf(message.getHeaders());
    Object payload = message.getPayload();
    try {
      IngestTelemetryCommand command = parser.parse(payload.toString());
      Optional<TelemetryView> stored = ingestTelemetry.execute(command);
      if (stored.isEmpty()) {
        log.warn("Dropped telemetry from topic '{}' (unknown or invalid device)", topic);
      } else {
        log.debug("Ingested telemetry id={} from topic '{}'", stored.get().id(), topic);
      }
    } catch (Exception ex) {
      log.error("Failed to process telemetry from topic '{}': {}", topic, ex.getMessage());
    }
  }
}
