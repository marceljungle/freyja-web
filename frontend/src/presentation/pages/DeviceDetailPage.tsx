import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useDevice } from "@/application/devices/useDevices";
import { useDeleteDevice } from "@/application/devices/useDeleteDevice";
import { useLatestTelemetry, useTrajectory } from "@/application/telemetry/useTelemetry";
import { useRequestLocation } from "@/application/command/useRequestLocation";
import { DeviceMap } from "@/presentation/components/map/DeviceMap";
import { BatteryBadge } from "@/presentation/components/BatteryBadge";
import { TemperatureBadge } from "@/presentation/components/TemperatureBadge";
import { DeviceStatus } from "@/presentation/components/DeviceStatus";
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
  const requestLocation = useRequestLocation(deviceId);
  const deleteDevice = useDeleteDevice();

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
        <div>
          <h1 className="text-xl font-semibold text-slate-800">{device?.name ?? "Device"}</h1>
          <p className="font-mono text-xs text-slate-400">{device?.imei}</p>
        </div>
        <div className="flex flex-wrap items-center gap-3">
          <DeviceStatus lastSeenAt={latest?.receivedAt ?? device?.lastSeenAt ?? null} />
          <BatteryBadge percent={latest?.batteryPercent ?? null} mv={latest?.batteryMv ?? null} />
          <TemperatureBadge celsius={latest?.temperatureC ?? null} />
          <button
            onClick={() => requestLocation.mutate()}
            disabled={requestLocation.isPending}
            className="btn-primary"
          >
            {requestLocation.isPending ? "Requesting…" : "Request location"}
          </button>
          <button
            onClick={() => setShowDelete(true)}
            className="rounded-lg border border-red-200 px-3 py-2 text-sm font-medium text-red-600 hover:bg-red-50"
          >
            Delete
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
