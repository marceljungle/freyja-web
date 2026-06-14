package com.freyja.application.device;

import java.util.UUID;

import com.freyja.application.common.UseCaseInput;
import com.freyja.domain.exception.ValidationException;

public record ListDevicesQuery(UUID ownerId) implements UseCaseInput {

  @Override
  public void validate() {
    if (ownerId == null) {
      throw new ValidationException("Owner is required");
    }
  }
}
