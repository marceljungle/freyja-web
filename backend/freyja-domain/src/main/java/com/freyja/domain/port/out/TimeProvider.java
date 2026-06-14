package com.freyja.domain.port.out;

import java.time.Instant;

public interface TimeProvider {

  Instant now();
}
