package com.freyja.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceRequest(
    @NotBlank String imei,
    @NotBlank String name,
    String fwVersion,
    String apn,
    String brokerIp,
    Integer brokerPort) {

}
