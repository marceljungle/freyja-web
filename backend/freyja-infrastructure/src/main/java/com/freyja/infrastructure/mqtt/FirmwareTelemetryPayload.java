package com.freyja.infrastructure.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wire model of the firmware MQTT telemetry payload (see {@code telemetry.c}).
 *
 * <pre>
 * // GPS fix
 * {"id":"352656100000000","reason":"motion","fix":true,
 *  "lat":12.345678,"lon":-1.234567,"acc":8.5,
 *  "utc":"2026-06-13T10:00:00Z","batt_mv":4012,"temp_c":24.5}
 *
 * // No fix: serving-cell info for a cell-tower location fallback
 * {"id":"352656100000000","reason":"boot","fix":false,"batt_mv":4012,"temp_c":24.5,
 *  "mcc":214,"mnc":7,"tac":37889,"cell_id":187848469}
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
    @JsonProperty("batt_mv") Integer battMv,
    @JsonProperty("temp_c") Double tempC,
    @JsonProperty("mcc") Integer mcc,
    @JsonProperty("mnc") Integer mnc,
    @JsonProperty("tac") Integer tac,
    @JsonProperty("cell_id") Integer cellId) {

}
