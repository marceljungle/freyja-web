package com.freyja.infrastructure.rest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.freyja.application.device.DeleteDeviceCommand;
import com.freyja.application.device.DeleteDeviceUseCase;
import com.freyja.application.device.DeviceView;
import com.freyja.application.device.GetDeviceQuery;
import com.freyja.application.device.GetDeviceUseCase;
import com.freyja.application.device.ListDevicesQuery;
import com.freyja.application.device.ListDevicesUseCase;
import com.freyja.application.device.RegisterDeviceCommand;
import com.freyja.application.device.RegisterDeviceUseCase;
import com.freyja.application.device.SetLiveModeCommand;
import com.freyja.application.device.SetLiveModeUseCase;
import com.freyja.application.telemetry.GetDeviceTrajectoryUseCase;
import com.freyja.application.telemetry.GetLatestTelemetryQuery;
import com.freyja.application.telemetry.GetLatestTelemetryUseCase;
import com.freyja.application.telemetry.GetTrajectoryQuery;
import com.freyja.application.telemetry.TelemetryView;
import com.freyja.infrastructure.rest.dto.LiveModeRequest;
import com.freyja.infrastructure.rest.dto.RegisterDeviceRequest;
import com.freyja.infrastructure.security.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

  private final RegisterDeviceUseCase registerDevice;

  private final ListDevicesUseCase listDevices;

  private final GetDeviceUseCase getDevice;

  private final DeleteDeviceUseCase deleteDevice;

  private final GetLatestTelemetryUseCase getLatestTelemetry;

  private final GetDeviceTrajectoryUseCase getDeviceTrajectory;

  private final SetLiveModeUseCase setLiveMode;

  public DeviceController(RegisterDeviceUseCase registerDevice,
      ListDevicesUseCase listDevices,
      GetDeviceUseCase getDevice,
      DeleteDeviceUseCase deleteDevice,
      GetLatestTelemetryUseCase getLatestTelemetry,
      GetDeviceTrajectoryUseCase getDeviceTrajectory,
      SetLiveModeUseCase setLiveMode) {
    this.registerDevice = registerDevice;
    this.listDevices = listDevices;
    this.getDevice = getDevice;
    this.deleteDevice = deleteDevice;
    this.getLatestTelemetry = getLatestTelemetry;
    this.getDeviceTrajectory = getDeviceTrajectory;
    this.setLiveMode = setLiveMode;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DeviceView register(@AuthenticationPrincipal AuthenticatedUser user,
      @Valid @RequestBody RegisterDeviceRequest request) {
    return registerDevice.execute(new RegisterDeviceCommand(
        user.id(), request.imei(), request.name(), request.fwVersion(),
        request.apn(), request.brokerIp(), request.brokerPort()));
  }

  @GetMapping
  public List<DeviceView> list(@AuthenticationPrincipal AuthenticatedUser user) {
    return listDevices.execute(new ListDevicesQuery(user.id()));
  }

  @GetMapping("/{deviceId}")
  public DeviceView get(@AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable UUID deviceId) {
    return getDevice.execute(new GetDeviceQuery(user.id(), deviceId));
  }

  @DeleteMapping("/{deviceId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable UUID deviceId) {
    deleteDevice.execute(new DeleteDeviceCommand(user.id(), deviceId));
  }

  @GetMapping("/{deviceId}/telemetry/latest")
  public ResponseEntity<TelemetryView> latestTelemetry(@AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable UUID deviceId) {
    return getLatestTelemetry.execute(new GetLatestTelemetryQuery(user.id(), deviceId))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  @GetMapping("/{deviceId}/telemetry")
  public List<TelemetryView> trajectory(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable UUID deviceId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
      @RequestParam(required = false) Integer limit) {
    return getDeviceTrajectory.execute(new GetTrajectoryQuery(user.id(), deviceId, from, to, limit));
  }

  @PostMapping("/{deviceId}/live-mode")
  public DeviceView setLiveMode(@AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable UUID deviceId,
      @Valid @RequestBody LiveModeRequest request) {
    return setLiveMode.execute(
        new SetLiveModeCommand(user.id(), deviceId, request.enabled(), request.interval()));
  }
}
