package com.freyja.infrastructure.mqtt;

import com.freyja.domain.exception.MessagingException;
import com.freyja.domain.port.out.MqttPublisher;
import com.freyja.domain.vo.Imei;
import com.freyja.infrastructure.config.MqttProperties;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

/**
 * {@link MqttPublisher} adapter. Builds the per-device command topic ({@code freyja/{imei}/rx}) and sends the payload through the outbound
 * MQTT integration channel (QoS 1, so Mosquitto queues it for the sleeping device).
 */
public class MqttPublisherAdapter implements MqttPublisher {

  private final MessageChannel outboundChannel;

  private final MqttProperties properties;

  public MqttPublisherAdapter(MessageChannel outboundChannel, MqttProperties properties) {
    this.outboundChannel = outboundChannel;
    this.properties = properties;
  }

  @Override
  public void publishToDevice(Imei imei, String payload) {
    String topic = properties.commandTopicFor(imei.value());
    Message<String> message = MessageBuilder.withPayload(payload)
        .setHeader(MqttHeaders.TOPIC, topic)
        .setHeader(MqttHeaders.QOS, properties.getQos())
        .build();
    try {
      boolean accepted = outboundChannel.send(message);
      if (!accepted) {
        throw new MessagingException("Broker rejected the message for topic " + topic);
      }
    } catch (MessagingException ex) {
      throw ex;
    } catch (RuntimeException ex) {
      throw new MessagingException("Failed to publish to topic " + topic, ex);
    }
  }
}
