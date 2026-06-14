import { useState } from "react";
import { useSerialProvisioning } from "@/application/provisioning/useSerialProvisioning";
import { useRegisterDevice } from "@/application/devices/useRegisterDevice";
import { apiErrorMessage } from "@/infrastructure/http/apiClient";

interface Props {
  onClose: () => void;
  onRegistered: () => void;
}

export function AddDeviceWizard({ onClose, onRegistered }: Props) {
  const provisioning = useSerialProvisioning();
  const registerDevice = useRegisterDevice();

  const [name, setName] = useState("");
  const [apn, setApn] = useState("hologram");
  const [brokerIp, setBrokerIp] = useState("");
  const [brokerPort, setBrokerPort] = useState(1883);
  const [submitError, setSubmitError] = useState<string | null>(null);

  const handleConnect = async () => {
    try {
      await provisioning.connect();
    } catch {
      /* error surfaced via provisioning.error */
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitError(null);
    if (!provisioning.info) return;
    try {
      await provisioning.sendConfig({ apn, brokerIp, brokerPort });
      await registerDevice.mutateAsync({
        imei: provisioning.info.imei,
        name,
        fwVersion: provisioning.info.fwVersion,
        apn,
        brokerIp,
        brokerPort,
      });
      await provisioning.disconnect();
      onRegistered();
    } catch (err) {
      setSubmitError(apiErrorMessage(err, "Failed to configure or register the device."));
    }
  };

  const close = async () => {
    await provisioning.reset();
    onClose();
  };

  return (
    <div className="fixed inset-0 z-[1000] flex items-center justify-center bg-slate-900/50 p-4">
      <div className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-slate-800">Add a device</h2>
          <button onClick={close} className="text-slate-400 hover:text-slate-600" aria-label="Close">
            ✕
          </button>
        </div>

        {!provisioning.supported && (
          <p className="mb-4 rounded-md bg-amber-50 p-3 text-sm text-amber-800">
            Web Serial isn't available here. Use Chrome or Edge over HTTPS or localhost.
          </p>
        )}

        {!provisioning.info ? (
          <div className="space-y-4">
            <ol className="list-decimal space-y-1 pl-5 text-sm text-slate-600">
              <li>Plug the Freyja tracker into this computer via USB.</li>
              <li>Click connect and select the device's serial port.</li>
            </ol>
            {provisioning.error && (
              <p className="rounded-md bg-red-50 p-3 text-sm text-red-700">{provisioning.error}</p>
            )}
            <button
              onClick={handleConnect}
              disabled={!provisioning.supported || provisioning.status === "connecting"}
              className="w-full rounded-lg bg-freyja-500 px-4 py-2 font-medium text-white hover:bg-freyja-600 disabled:opacity-50"
            >
              {provisioning.status === "connecting" ? "Connecting…" : "Connect device"}
            </button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="rounded-md bg-slate-50 p-3 text-sm">
              <div className="flex justify-between">
                <span className="text-slate-500">IMEI</span>
                <span className="font-mono text-slate-800">{provisioning.info.imei}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Firmware</span>
                <span className="text-slate-800">{provisioning.info.fwVersion}</span>
              </div>
            </div>

            <Field label="Device name">
              <input
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="e.g. Motorcycle"
                className="input"
              />
            </Field>
            <Field label="APN">
              <input required value={apn} onChange={(e) => setApn(e.target.value)} className="input" />
            </Field>
            <div className="grid grid-cols-2 gap-3">
              <Field label="Broker IP / host">
                <input
                  required
                  value={brokerIp}
                  onChange={(e) => setBrokerIp(e.target.value)}
                  placeholder="203.0.113.5"
                  className="input"
                />
              </Field>
              <Field label="Broker port">
                <input
                  required
                  type="number"
                  value={brokerPort}
                  onChange={(e) => setBrokerPort(Number(e.target.value))}
                  className="input"
                />
              </Field>
            </div>

            {submitError && <p className="rounded-md bg-red-50 p-3 text-sm text-red-700">{submitError}</p>}

            <button
              type="submit"
              disabled={provisioning.status === "configuring" || registerDevice.isPending}
              className="w-full rounded-lg bg-freyja-500 px-4 py-2 font-medium text-white hover:bg-freyja-600 disabled:opacity-50"
            >
              {provisioning.status === "configuring"
                ? "Configuring device…"
                : registerDevice.isPending
                  ? "Registering…"
                  : "Configure & register"}
            </button>
          </form>
        )}
      </div>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <label className="block">
      <span className="mb-1 block text-xs font-medium text-slate-500">{label}</span>
      {children}
    </label>
  );
}
