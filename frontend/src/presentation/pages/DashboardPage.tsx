import { useState } from "react";
import { Link } from "react-router-dom";
import { useDevices } from "@/application/devices/useDevices";
import { apiErrorMessage } from "@/infrastructure/http/apiClient";
import { AddDeviceWizard } from "@/presentation/components/AddDeviceWizard";
import { DeviceStatus } from "@/presentation/components/DeviceStatus";

export function DashboardPage() {
  const { data: devices, isLoading, isError, error, refetch } = useDevices();
  const [showWizard, setShowWizard] = useState(false);

  return (
    <div>
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-xl font-semibold text-slate-800">Your devices</h1>
        <button onClick={() => setShowWizard(true)} className="btn-primary">
          + Add device
        </button>
      </div>

      {isLoading && <p className="text-sm text-slate-500">Loading devices…</p>}
      {isError && (
        <p className="rounded-md bg-red-50 p-3 text-sm text-red-700">
          {apiErrorMessage(error, "Failed to load devices.")}
        </p>
      )}

      {devices && devices.length === 0 && (
        <div className="card flex flex-col items-center gap-3 p-10 text-center">
          <p className="text-slate-500">No devices yet. Provision your first tracker over USB.</p>
          <button onClick={() => setShowWizard(true)} className="btn-primary">
            + Add device
          </button>
        </div>
      )}

      {devices && devices.length > 0 && (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {devices.map((device) => (
            <Link
              key={device.id}
              to={`/devices/${device.id}`}
              className="card p-4 transition hover:border-freyja-500 hover:shadow"
            >
              <div className="flex items-start justify-between">
                <h2 className="font-semibold text-slate-800">{device.name}</h2>
              </div>
              <p className="mt-1 font-mono text-xs text-slate-400">{device.imei}</p>
              <div className="mt-3">
                <DeviceStatus lastSeenAt={device.lastSeenAt} />
              </div>
            </Link>
          ))}
        </div>
      )}

      {showWizard && (
        <AddDeviceWizard
          onClose={() => setShowWizard(false)}
          onRegistered={() => {
            setShowWizard(false);
            void refetch();
          }}
        />
      )}
    </div>
  );
}
