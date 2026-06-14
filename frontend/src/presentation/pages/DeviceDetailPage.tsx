import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { useDevice } from "@/application/devices/useDevices";
import { useLatestTelemetry, useTrajectory } from "@/application/telemetry/useTelemetry";
import { useRequestLocation } from "@/application/command/useRequestLocation";
import { DeviceMap } from "@/presentation/components/map/DeviceMap";
import { BatteryBadge } from "@/presentation/components/BatteryBadge";
import { DeviceStatus } from "@/presentation/components/DeviceStatus";
import {
  DEFAULT_RANGE,
  TimeRangeFilter,
  trajectoryRange,
} from "@/presentation/components/TimeRangeFilter";
import { apiErrorMessage } from "@/infrastructure/http/apiClient";
import { formatDateTime } from "@/shared/format";

export function DeviceDetailPage() {
  const { deviceId } = useParams<{ deviceId: string }>();
  const [range, setRange] = useState(DEFAULT_RANGE);

  const { data: device } = useDevice(deviceId);
  const { data: latest } = useLatestTelemetry(deviceId);
  const { data: trajectory } = useTrajectory(deviceId, trajectoryRange(range));
  const requestLocation = useRequestLocation(deviceId);

  const lastFix = latest?.hasFix ? latest : null;

  return (
    <div>
      <div className="mb-4">
        <Link to="/" className="text-sm text-freyja-600 hover:underline">
          ← Back to devices
        </Link>
      </div>

      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">{device?.name ?? "Device"}</h1>
          <p className="font-mono text-xs text-slate-400">{device?.imei}</p>
        </div>
        <div className="flex items-center gap-3">
          <DeviceStatus lastSeenAt={device?.lastSeenAt ?? null} />
          <BatteryBadge percent={latest?.batteryPercent ?? null} mv={latest?.batteryMv ?? null} />
          <button
            onClick={() => requestLocation.mutate()}
            disabled={requestLocation.isPending}
            className="btn-primary"
          >
            {requestLocation.isPending ? "Requesting…" : "Request location"}
          </button>
        </div>
      </div>

      {requestLocation.isSuccess && (
        <p className="mb-3 rounded-md bg-emerald-50 p-2 text-sm text-emerald-700">
          Location request queued. The device will report when it next wakes.
        </p>
      )}
      {requestLocation.isError && (
        <p className="mb-3 rounded-md bg-red-50 p-2 text-sm text-red-700">
          {apiErrorMessage(requestLocation.error, "Failed to queue the command.")}
        </p>
      )}

      <div className="grid gap-4 lg:grid-cols-[1fr_300px]">
        <div className="card overflow-hidden" style={{ height: "70vh" }}>
          <DeviceMap latest={latest ?? null} trajectory={trajectory ?? []} />
        </div>

        <aside className="space-y-4">
          <div className="card p-4">
            <div className="mb-2 flex items-center justify-between">
              <h2 className="text-sm font-semibold text-slate-700">Trajectory</h2>
            </div>
            <TimeRangeFilter value={range} onChange={setRange} />
            <p className="mt-3 text-xs text-slate-400">
              {trajectory?.filter((t) => t.hasFix).length ?? 0} fixes in range
            </p>
          </div>

          <div className="card p-4 text-sm">
            <h2 className="mb-2 text-sm font-semibold text-slate-700">Last fix</h2>
            {lastFix ? (
              <dl className="space-y-1 text-slate-600">
                <Row label="Latitude" value={lastFix.latitude?.toFixed(6)} />
                <Row label="Longitude" value={lastFix.longitude?.toFixed(6)} />
                <Row
                  label="Accuracy"
                  value={lastFix.accuracy != null ? `${Math.round(lastFix.accuracy)} m` : "—"}
                />
                <Row label="Device time" value={formatDateTime(lastFix.deviceTime)} />
                <Row label="Received" value={formatDateTime(lastFix.receivedAt)} />
              </dl>
            ) : (
              <p className="text-slate-400">
                {latest ? "Last report had no GPS fix." : "No telemetry received yet."}
              </p>
            )}
          </div>
        </aside>
      </div>
    </div>
  );
}

function Row({ label, value }: { label: string; value: string | number | undefined }) {
  return (
    <div className="flex justify-between">
      <dt className="text-slate-400">{label}</dt>
      <dd className="font-medium text-slate-700">{value ?? "—"}</dd>
    </div>
  );
}
