package com.freyja.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "freyja.mqtt")
public class MqttProperties {

  /**
   * Whether MQTT integration is active. Disable in tests/offline setups.
   */
  private boolean enabled = true;

  private String url = "tcp://localhost:1883";

  private String clientId = "freyja-backend";

  private String username = "";

  private String password = "";

  private String telemetryTopic = "freyja/+/telemetry";

  private String commandTopicPattern = "freyja/%s/rx";

  private int qos = 1;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getTelemetryTopic() {
    return telemetryTopic;
  }

  public void setTelemetryTopic(String telemetryTopic) {
    this.telemetryTopic = telemetryTopic;
  }

  public String getCommandTopicPattern() {
    return commandTopicPattern;
  }

  public void setCommandTopicPattern(String commandTopicPattern) {
    this.commandTopicPattern = commandTopicPattern;
  }

  public int getQos() {
    return qos;
  }

  public void setQos(int qos) {
    this.qos = qos;
  }

  public String commandTopicFor(String imei) {
    return String.format(commandTopicPattern, imei);
  }
}
