import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useDevice } from "@/application/devices/useDevices";
import { useDeleteDevice } from "@/application/devices/useDeleteDevice";
import { useSetLiveMode } from "@/application/devices/useSetLiveMode";
import { useLatestTelemetry, useTrajectory } from "@/application/telemetry/useTelemetry";
import { DeviceMap } from "@/presentation/components/map/DeviceMap";
import { BatteryBadge } from "@/presentation/components/BatteryBadge";
import { TemperatureBadge } from "@/presentation/components/TemperatureBadge";
import { DeviceStatus } from "@/presentation/components/DeviceStatus";
import { DeviceHealth } from "@/presentation/components/DeviceHealth";
import { DeleteDeviceDialog } from "@/presentation/components/DeleteDeviceDialog";
import {
  DEFAULT_RANGE,
  TimeRangeFilter,
  trajectoryRange,
} from "@/presentation/components/TimeRangeFilter";
import { apiErrorMessage } from "@/infrastructure/http/apiClient";
import { formatDateTime } from "@/shared/format";

export function DeviceDetailPage() {
  const { deviceId } = useParams<{ deviceId: string }>();
  const navigate = useNavigate();
  const [range, setRange] = useState(DEFAULT_RANGE);
  const [showDelete, setShowDelete] = useState(false);

  // Recompute the trajectory time window only when the preset changes or every
  // 30s — NOT on every render. Otherwise trajectoryRange()'s `to = now` would
  // change the query key continuously and refetch in a tight loop.
  const [windowTick, setWindowTick] = useState(0);
  useEffect(() => {
    const id = window.setInterval(() => setWindowTick((t) => t + 1), 30_000);
    return () => window.clearInterval(id);
  }, []);
  const trajectoryQuery = useMemo(() => trajectoryRange(range), [range, windowTick]);

  const { data: device } = useDevice(deviceId);
  const { data: latest } = useLatestTelemetry(deviceId);
  const { data: trajectory } = useTrajectory(deviceId, trajectoryQuery);
  const setLiveMode = useSetLiveMode(deviceId);
  const deleteDevice = useDeleteDevice();

  const liveActive = device?.liveModeEnabled ?? false;

  // A reading is "located" when it has a GPS fix or a cell-tower approximation.
  const located = latest && (latest.hasFix || latest.approximate) && latest.latitude != null
    ? latest
    : null;

  const handleDelete = () => {
    if (!deviceId) return;
    deleteDevice.mutate(deviceId, { onSuccess: () => navigate("/", { replace: true }) });
  };

  return (
    <div>
      <div className="mb-4">
        <Link to="/" className="text-sm text-freyja-600 hover:underline">
          ← Back to devices
        </Link>
      </div>

      <div className="mb-4 flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">{device?.name ?? "Device"}</h1>
            <p className="font-mono text-xs text-slate-400">{device?.imei}</p>
          </div>
          {liveActive && (
            <span className="inline-flex items-center gap-1.5 rounded-full bg-red-50 px-2 py-0.5 text-xs font-semibold text-red-600">
              <span className="h-2 w-2 animate-pulse rounded-full bg-red-500" />
              LIVE
            </span>
          )}
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <DeviceStatus lastSeenAt={latest?.receivedAt ?? device?.lastSeenAt ?? null} />
          <BatteryBadge percent={latest?.batteryPercent ?? null} mv={latest?.batteryMv ?? null} />
          <TemperatureBadge celsius={latest?.temperatureC ?? null} />
          {liveActive ? (
            <button
              onClick={() => setLiveMode.mutate({ enabled: false })}
              disabled={setLiveMode.isPending}
              className="inline-flex items-center gap-2 rounded-lg bg-red-600 px-4 py-2 font-medium text-white hover:bg-red-700 disabled:opacity-50"
            >
              {setLiveMode.isPending ? "Stopping…" : "Stop live mode"}
            </button>
          ) : (
            <button
              onClick={() => setLiveMode.mutate({ enabled: true })}
              disabled={setLiveMode.isPending}
              className="btn-primary"
            >
              {setLiveMode.isPending ? "Starting…" : "Start live mode"}
            </button>
          )}
          <button
            onClick={() => setShowDelete(true)}
            className="rounded-lg border border-red-200 px-3 py-2 text-sm font-medium text-red-600 hover:bg-red-50"
          >
            Delete
          </button>
        </div>
      </div>

      {liveActive && (
        <p className="mb-3 rounded-md bg-amber-50 p-2 text-sm text-amber-800">
          <span className="font-semibold">Live mode is on.</span> The tracker streams its position
          continuously once it's awake — instantly if moving, otherwise on its next heartbeat
          (up to ~30 min if it's been still). This drains the battery quickly, so turn it off when
          you're done (it won't stop on its own).
        </p>
      )}
      {setLiveMode.isError && (
        <p className="mb-3 rounded-md bg-red-50 p-2 text-sm text-red-700">
          {apiErrorMessage(setLiveMode.error, "Failed to change live mode.")}
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
              {trajectory?.filter((t) => t.hasFix).length ?? 0} GPS fixes in range
            </p>
          </div>

          <div className="card p-4 text-sm">
            <h2 className="mb-2 text-sm font-semibold text-slate-700">Last report</h2>
            {latest ? (
              <dl className="space-y-1 text-slate-600">
                <Row label="Reason" value={latest.reason ?? "—"} />
                {located ? (
                  <>
                    <Row label="Source" value={latest.hasFix ? "GPS" : "Approx. (cell tower)"} />
                    <Row label="Latitude" value={located.latitude?.toFixed(6)} />
                    <Row label="Longitude" value={located.longitude?.toFixed(6)} />
                    <Row
                      label="Accuracy"
                      value={located.accuracy != null ? `${Math.round(located.accuracy)} m` : "—"}
                    />
                    {latest.hasFix && (
                      <Row label="Device time" value={formatDateTime(latest.deviceTime)} />
                    )}
                  </>
                ) : (
                  <Row label="Location" value="none (no GPS / cell)" />
                )}
                <Row label="Received" value={formatDateTime(latest.receivedAt)} />
              </dl>
            ) : (
              <p className="text-slate-400">No telemetry received yet.</p>
            )}
          </div>

          <DeviceHealth latest={latest ?? null} />
        </aside>
      </div>

      {showDelete && (
        <DeleteDeviceDialog
          deviceName={device?.name ?? "this device"}
          pending={deleteDevice.isPending}
          error={
            deleteDevice.isError
              ? apiErrorMessage(deleteDevice.error, "Failed to delete the device.")
              : null
          }
          onCancel={() => setShowDelete(false)}
          onConfirm={handleDelete}
        />
      )}
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
