package com.freyja.application.command;

import java.time.Instant;
import java.util.UUID;

import com.freyja.domain.model.command.DeviceCommand;

public record CommandView(
    UUID id,
    UUID deviceId,
    String type,
    String status,
    Instant createdAt,
    Instant sentAt,
    Instant acknowledgedAt) {

  public static CommandView from(DeviceCommand command) {
    return new CommandView(
        command.id(),
        command.deviceId(),
        command.type().name(),
        command.status().name(),
        command.createdAt(),
        command.sentAt(),
        command.acknowledgedAt());
  }
}
