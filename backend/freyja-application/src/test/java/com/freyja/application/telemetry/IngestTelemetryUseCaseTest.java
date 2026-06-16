package com.freyja.application.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.device.Device;
import com.freyja.domain.port.out.CellLocationResolver;
import com.freyja.domain.port.out.DeviceCommandRepository;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.TelemetryRepository;
import com.freyja.domain.port.out.TimeProvider;
import com.freyja.domain.vo.CellLocation;
import com.freyja.domain.vo.Coordinates;
import com.freyja.domain.vo.Imei;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IngestTelemetryUseCaseTest {

  private static final String IMEI = "352656100000000";

  @Mock
  DeviceRepository deviceRepository;

  @Mock
  TelemetryRepository telemetryRepository;

  @Mock
  DeviceCommandRepository commandRepository;

  @Mock
  CellLocationResolver cellLocationResolver;

  @Mock
  TimeProvider time;

  @InjectMocks
  IngestTelemetryUseCase useCase;

  private static IngestTelemetryCommand command(boolean hasFix, Double lat, Double lon,
      Double acc, Integer battMv, Double tempC, Integer mcc, Integer mnc, Integer tac, Integer cellId) {
    return new IngestTelemetryCommand(IMEI, "motion", hasFix, lat, lon, acc, battMv, tempC,
        mcc, mnc, tac, cellId, null);
  }

  @Test
  void dropsTelemetryFromUnregisteredDevice() {
    when(deviceRepository.findByImei(any())).thenReturn(Optional.empty());

    Optional<TelemetryView> result = useCase.execute(
        command(false, null, null, null, 4000, null, null, null, null, null));

    assertTrue(result.isEmpty());
    verify(telemetryRepository, never()).save(any());
  }

  @Test
  void storesFixWithTemperatureAndMarksDeviceSeen() {
    registerDevice();
    when(time.now()).thenReturn(Instant.parse("2026-06-14T10:00:00Z"));
    when(telemetryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Optional<TelemetryView> result = useCase.execute(new IngestTelemetryCommand(
        IMEI, "motion", true, 40.4168, -3.7038, 6.0, 3850, 24.5,
        null, null, null, null, Instant.parse("2026-06-14T09:30:00Z")));

    assertTrue(result.isPresent());
    TelemetryView view = result.get();
    assertTrue(view.hasFix());
    assertFalse(view.approximate());
    assertEquals(40.4168, view.latitude(), 1e-6);
    assertEquals(61, view.batteryPercent());
    assertEquals(24.5, view.temperatureC(), 1e-6);
    verify(deviceRepository).save(any());
  }

  @Test
  void resolvesApproximateLocationFromCellTowerWhenNoFix() {
    registerDevice();
    when(time.now()).thenReturn(Instant.parse("2026-06-14T10:00:00Z"));
    when(telemetryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(cellLocationResolver.resolve(any()))
        .thenReturn(Optional.of(CellLocation.of(Coordinates.of(10.0, 20.0), 1500.0)));

    Optional<TelemetryView> result = useCase.execute(
        command(false, null, null, null, 3850, 24.5, 214, 7, 37889, 187848469));

    assertTrue(result.isPresent());
    TelemetryView view = result.get();
    assertFalse(view.hasFix());
    assertTrue(view.approximate());
    assertEquals(10.0, view.latitude(), 1e-6);
    assertEquals(20.0, view.longitude(), 1e-6);
    assertEquals(1500.0, view.accuracy(), 1e-6);
    assertEquals(214, view.mcc());
    verify(cellLocationResolver).resolve(any());
  }

  @Test
  void storesWithoutLocationWhenCellLookupFails() {
    registerDevice();
    when(time.now()).thenReturn(Instant.parse("2026-06-14T10:00:00Z"));
    when(telemetryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(cellLocationResolver.resolve(any())).thenReturn(Optional.empty());

    Optional<TelemetryView> result = useCase.execute(
        command(false, null, null, null, 3850, 24.5, 214, 7, 37889, 187848469));

    assertTrue(result.isPresent());
    TelemetryView view = result.get();
    assertFalse(view.hasFix());
    assertFalse(view.approximate());
    assertNull(view.latitude());
  }

  private void registerDevice() {
    Device device = Device.register(Imei.of(IMEI), "Moto", "1.0.0",
        UUID.randomUUID(), null, Instant.parse("2026-01-01T00:00:00Z"));
    when(deviceRepository.findByImei(any())).thenReturn(Optional.of(device));
  }
}
