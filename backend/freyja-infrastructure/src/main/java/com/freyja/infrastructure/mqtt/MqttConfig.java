package com.freyja.infrastructure.mqtt;

import com.freyja.domain.port.out.MqttPublisher;
import com.freyja.infrastructure.config.MqttProperties;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;

/**
 * Spring Integration MQTT wiring (active unless {@code freyja.mqtt.enabled=false}).
 *
 * <ul>
 *   <li>Inbound: subscribes to {@code freyja/+/telemetry} and routes payloads to
 *       {@link TelemetryMqttHandler}.</li>
 *   <li>Outbound: publishes device commands (QoS 1) via {@link MqttPublisherAdapter}.</li>
 * </ul>
 */
@Configuration
@ConditionalOnProperty(prefix = "freyja.mqtt", name = "enabled", havingValue = "true", matchIfMissing = true)
public class MqttConfig {

  @Bean
  public MqttPahoClientFactory mqttClientFactory(MqttProperties props) {
    DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
    MqttConnectOptions options = new MqttConnectOptions();
    options.setServerURIs(new String[]{props.getUrl()});
    options.setCleanSession(true);
    options.setAutomaticReconnect(true);
    if (StringUtils.hasText(props.getUsername())) {
      options.setUserName(props.getUsername());
      options.setPassword(props.getPassword() != null ? props.getPassword().toCharArray() : new char[0]);
    }
    factory.setConnectionOptions(options);
    return factory;
  }

  // ---- Inbound (telemetry subscription) ------------------------------------

  @Bean
  public MessageChannel mqttInboundChannel() {
    return new DirectChannel();
  }

  @Bean
  public MqttPahoMessageDrivenChannelAdapter mqttInboundAdapter(
      MqttPahoClientFactory factory,
      MqttProperties props,
      @Qualifier("mqttInboundChannel") MessageChannel inboundChannel) {
    MqttPahoMessageDrivenChannelAdapter adapter =
        new MqttPahoMessageDrivenChannelAdapter(
            props.getClientId() + "-in", factory, props.getTelemetryTopic());
    adapter.setQos(props.getQos());
    adapter.setCompletionTimeout(5000);
    adapter.setConverter(new DefaultPahoMessageConverter());
    adapter.setOutputChannel(inboundChannel);
    return adapter;
  }

  @Bean
  @ServiceActivator(inputChannel = "mqttInboundChannel")
  public MessageHandler mqttTelemetryActivator(TelemetryMqttHandler handler) {
    return handler::handle;
  }

  // ---- Outbound (command publishing) ---------------------------------------

  @Bean
  public MessageChannel mqttOutboundChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "mqttOutboundChannel")
  public MessageHandler mqttOutboundHandler(MqttPahoClientFactory factory, MqttProperties props) {
    MqttPahoMessageHandler handler =
        new MqttPahoMessageHandler(props.getClientId() + "-out", factory);
    handler.setAsync(false);
    handler.setDefaultQos(props.getQos());
    handler.setDefaultRetained(false);
    return handler;
  }

  @Bean
  public MqttPublisher mqttPublisher(@Qualifier("mqttOutboundChannel") MessageChannel outboundChannel,
      MqttProperties props) {
    return new MqttPublisherAdapter(outboundChannel, props);
  }
}
