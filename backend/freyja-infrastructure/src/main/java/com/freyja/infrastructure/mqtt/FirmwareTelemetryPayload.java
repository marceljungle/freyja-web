package com.freyja.infrastructure.mqtt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Wire model of the firmware MQTT telemetry payload (see {@code telemetry.c}).
 *
 * <pre>
 * // Live status (GNSS health + cell info + rsrp sent even when fix:false)
 * {"id":"...","reason":"motion","fix":true,"lat":..,"lon":..,"acc":..,"utc":"..",
 *  "batt_mv":4012,"temp_c":24.5,"tracked_svs":12,"svs_used":7,"cn0":43.1,
 *  "mcc":214,"mnc":7,"tac":37889,"cell_id":187848469,"rsrp":-98}
 *
 * // Buffered fix replayed from the device's offline buffer
 * {"id":"...","reason":"motion","fix":true,"lat":..,"lon":..,"acc":..,"utc":"..",
 *  "batt_mv":4012,"temp_c":24.5,"tracked_svs":12,"svs_used":7,"cn0":43.1,"buffered":true}
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record FirmwareTelemetryPayload(
    @JsonProperty("id") String id,
    @JsonProperty("reason") String reason,
    @JsonProperty("fix") Boolean fix,
    @JsonProperty("buffered") Boolean buffered,
    @JsonProperty("lat") Double lat,
    @JsonProperty("lon") Double lon,
    @JsonProperty("acc") Double acc,
    @JsonProperty("utc") String utc,
    @JsonProperty("batt_mv") Integer battMv,
    @JsonProperty("temp_c") Double tempC,
    @JsonProperty("rsrp") Integer rsrp,
    @JsonProperty("tracked_svs") Integer trackedSvs,
    @JsonProperty("svs_used") Integer svsUsed,
    @JsonProperty("cn0") Double cn0,
    @JsonProperty("mcc") Integer mcc,
    @JsonProperty("mnc") Integer mnc,
    @JsonProperty("tac") Integer tac,
    @JsonProperty("cell_id") Integer cellId) {

}
