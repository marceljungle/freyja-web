package com.freyja.infrastructure.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wire model of the firmware MQTT telemetry payload (see {@code telemetry.c}).
 *
 * <pre>
 * {"id":"352656100000000","reason":"motion","fix":true,
 *  "lat":12.345678,"lon":-1.234567,"acc":8.5,
 *  "utc":"2026-06-13T10:00:00Z","batt_mv":4012}
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FirmwareTelemetryPayload(
    @JsonProperty("id") String id,
    @JsonProperty("reason") String reason,
    @JsonProperty("fix") Boolean fix,
    @JsonProperty("lat") Double lat,
    @JsonProperty("lon") Double lon,
    @JsonProperty("acc") Double acc,
    @JsonProperty("utc") String utc,
    @JsonProperty("batt_mv") Integer battMv) {

}
