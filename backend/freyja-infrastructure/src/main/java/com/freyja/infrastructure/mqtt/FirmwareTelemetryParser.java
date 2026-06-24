package com.freyja.infrastructure.mqtt;

import java.time.Instant;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freyja.application.telemetry.IngestTelemetryCommand;
import org.springframework.stereotype.Component;

@Component
public class FirmwareTelemetryParser {

  private final ObjectMapper objectMapper;

  public FirmwareTelemetryParser(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  private static Instant parseUtc(String utc) {
    if (utc == null || utc.isBlank()) {
      return null;
    }
    try {
      return Instant.parse(utc);
    } catch (DateTimeParseException ex) {
      return null; // tolerate a malformed timestamp; the receive time still applies
    }
  }

  /**
   * @throws JsonProcessingException if the payload is not valid JSON.
   */
  public IngestTelemetryCommand parse(String json) throws JsonProcessingException {
    FirmwareTelemetryPayload p = objectMapper.readValue(json, FirmwareTelemetryPayload.class);
    boolean hasFix = Boolean.TRUE.equals(p.fix());
    boolean buffered = Boolean.TRUE.equals(p.buffered());
    // The firmware reports rsrp 0 when unknown; treat it as "no reading".
    Integer rsrp = p.rsrp() != null && p.rsrp() != 0 ? p.rsrp() : null;
    return new IngestTelemetryCommand(
        p.id(),
        p.reason(),
        hasFix,
        buffered,
        hasFix ? p.lat() : null,
        hasFix ? p.lon() : null,
        hasFix ? p.acc() : null,
        p.battMv(),
        p.tempC(),
        rsrp,
        p.trackedSvs(),
        p.svsUsed(),
        p.cn0(),
        p.mcc(),
        p.mnc(),
        p.tac(),
        p.cellId(),
        parseUtc(p.utc()));
  }
}
