package com.freyja.infrastructure.location;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freyja.domain.exception.ValidationException;
import com.freyja.domain.port.out.CellLocationResolver;
import com.freyja.domain.vo.CellLocation;
import com.freyja.domain.vo.CellTower;
import com.freyja.domain.vo.Coordinates;
import com.freyja.infrastructure.config.OpenCellIdProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * {@link CellLocationResolver} backed by the OpenCelliD API
 * ({@code GET /cell/get?...&format=json}). Fails soft: any error (missing key,
 * timeout, unknown cell, malformed response) yields an empty result so telemetry
 * ingestion is never blocked.
 */
@Component
public class OpenCellIdLocationResolver implements CellLocationResolver {

  private static final Logger log = LoggerFactory.getLogger(OpenCellIdLocationResolver.class);

  private final OpenCellIdProperties properties;

  private final RestClient restClient;

  public OpenCellIdLocationResolver(OpenCellIdProperties properties) {
    this.properties = properties;
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(properties.getTimeoutMs());
    factory.setReadTimeout(properties.getTimeoutMs());
    this.restClient = RestClient.builder()
        .baseUrl(properties.getBaseUrl())
        .requestFactory(factory)
        .build();
  }

  @Override
  public Optional<CellLocation> resolve(CellTower tower) {
    if (!properties.isConfigured()) {
      log.debug("OpenCelliD lookup skipped: no API key configured");
      return Optional.empty();
    }
    try {
      OpenCellIdResponse response = restClient.get()
          .uri(uri -> uri.path("/cell/get")
              .queryParam("key", properties.getApiKey())
              .queryParam("mcc", tower.mcc())
              .queryParam("mnc", tower.mnc())
              .queryParam("lac", tower.tac())
              .queryParam("cellid", tower.cellId())
              .queryParam("format", "json")
              .build())
          .retrieve()
          .body(OpenCellIdResponse.class);

      if (response == null || response.lat() == null || response.lon() == null) {
        log.debug("OpenCelliD returned no location for cell {}/{}/{}/{}",
            tower.mcc(), tower.mnc(), tower.tac(), tower.cellId());
        return Optional.empty();
      }

      double accuracy = response.range() != null && response.range() > 0
          ? response.range()
          : properties.getDefaultAccuracyMeters();
      Coordinates coordinates = Coordinates.of(response.lat(), response.lon());
      return Optional.of(CellLocation.of(coordinates, accuracy));
    } catch (ValidationException invalid) {
      log.warn("OpenCelliD returned invalid coordinates: {}", invalid.getMessage());
      return Optional.empty();
    } catch (RuntimeException ex) {
      log.warn("OpenCelliD lookup failed for cell {}/{}/{}/{}: {}",
          tower.mcc(), tower.mnc(), tower.tac(), tower.cellId(), ex.getMessage());
      return Optional.empty();
    }
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record OpenCellIdResponse(Double lat, Double lon, Integer range) {
  }
}
