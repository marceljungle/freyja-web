package com.freyja.domain.vo;

import com.freyja.domain.exception.ValidationException;

public record NetworkConfig(String apn, String brokerIp, int brokerPort) {

  private static final int APN_MAX = 32;

  private static final int HOST_MAX = 64;

  public NetworkConfig {
    if (apn == null || apn.isBlank()) {
      throw new ValidationException("APN must not be blank");
    }
    if (brokerIp == null || brokerIp.isBlank()) {
      throw new ValidationException("Broker IP/host must not be blank");
    }
    apn = apn.trim();
    brokerIp = brokerIp.trim();
    if (apn.length() > APN_MAX) {
      throw new ValidationException("APN must be at most " + APN_MAX + " characters");
    }
    if (brokerIp.length() > HOST_MAX) {
      throw new ValidationException("Broker IP/host must be at most " + HOST_MAX + " characters");
    }
    if (brokerPort < 1 || brokerPort > 65535) {
      throw new ValidationException("Broker port must be between 1 and 65535");
    }
  }

  public static NetworkConfig of(String apn, String brokerIp, int brokerPort) {
    return new NetworkConfig(apn, brokerIp, brokerPort);
  }
}
