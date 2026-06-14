package com.freyja.domain.port.out;

import com.freyja.domain.vo.Imei;

public interface MqttPublisher {

  /**
   * Publish a raw JSON payload to the given device's command topic.
   *
   * @throws com.freyja.domain.exception.DomainException if publishing fails.
   */
  void publishToDevice(Imei imei, String payload);
}
