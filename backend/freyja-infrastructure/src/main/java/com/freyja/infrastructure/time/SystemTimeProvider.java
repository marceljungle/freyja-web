package com.freyja.infrastructure.time;

import java.time.Instant;

import com.freyja.domain.port.out.TimeProvider;
import org.springframework.stereotype.Component;

@Component
public class SystemTimeProvider implements TimeProvider {

  @Override
  public Instant now() {
    return Instant.now();
  }
}
