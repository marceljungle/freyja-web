package com.freyja.application.telemetry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.freyja.domain.model.device.Device;
import com.freyja.domain.model.telemetry.TelemetryData;
import com.freyja.domain.port.out.DeviceCommandRepository;
import com.freyja.domain.port.out.DeviceRepository;
import com.freyja.domain.port.out.TelemetryRepository;
import com.freyja.domain.port.out.TimeProvider;
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
  TimeProvider time;

  @InjectMocks
  IngestTelemetryUseCase useCase;

  @Test
  void dropsTelemetryFromUnregisteredDevice() {
    when(deviceRepository.findByImei(any())).thenReturn(Optional.empty());

    Optional<TelemetryView> result = useCase.execute(
        new IngestTelemetryCommand(IMEI, "motion", false, null, null, null, 4000, null));

    assertTrue(result.isEmpty());
    verify(telemetryRepository, never()).save(any());
  }

  @Test
  void storesFixAndMarksDeviceSeen() {
    Device device = Device.register(Imei.of(IMEI), "Moto", "1.0.0",
        UUID.randomUUID(), null, Instant.parse("2026-01-01T00:00:00Z"));
    when(deviceRepository.findByImei(any())).thenReturn(Optional.of(device));
    when(time.now()).thenReturn(Instant.parse("2026-06-14T10:00:00Z"));
    when(telemetryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Optional<TelemetryView> result = useCase.execute(new IngestTelemetryCommand(
        IMEI, "motion", true, 40.4168, -3.7038, 6.0, 3850,
        Instant.parse("2026-06-14T09:30:00Z")));

    assertTrue(result.isPresent());
    TelemetryView view = result.get();
    assertTrue(view.hasFix());
    assertEquals(40.4168, view.latitude(), 1e-6);
    assertEquals(61, view.batteryPercent());
    // device.markSeen(...) persisted
    verify(deviceRepository).save(any());
    TelemetryData saved = TelemetryData.withoutFix(device.id(), "motion", null,
        Instant.parse("2026-06-14T10:00:00Z"));
    assertFalse(saved.hasFix()); // sanity on factory
  }
}
