package com.freyja.application.device;

import java.util.UUID;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

public record RegisterDeviceCommand(
    UUID ownerId,
    String imei,
    String name,
    String fwVersion,
    String apn,
    String brokerIp,
    Integer brokerPort) implements UseCaseInput {

  @Override
  public void validate() {
    if (ownerId == null) {
      throw new ValidationException("Owner is required");
    }
    if (imei == null || imei.isBlank()) {
      throw new ValidationException("IMEI must not be blank");
    }
    if (name == null || name.isBlank()) {
      throw new ValidationException("Device name must not be blank");
    }
    boolean anyNetwork = apn != null || brokerIp != null || brokerPort != null;
    boolean allNetwork = apn != null && brokerIp != null && brokerPort != null;
    if (anyNetwork && !allNetwork) {
      throw new ValidationException("APN, broker IP and broker port must all be provided together");
    }
  }

  public boolean hasNetworkConfig() {
    return apn != null && brokerIp != null && brokerPort != null;
  }
}
