import { useCallback, useRef, useState } from "react";
import {
  SerialProvisioner,
  isWebSerialSupported,
  type DeviceInfo,
  type NetworkConfigInput,
} from "@/infrastructure/serial/serialProvisioner";

export type ProvisioningStatus =
  | "idle"
  | "connecting"
  | "connected"
  | "configuring"
  | "configured"
  | "error";

interface State {
  status: ProvisioningStatus;
  info: DeviceInfo | null;
  error: string | null;
}

const INITIAL: State = { status: "idle", info: null, error: null };

/**
 * Stateful wrapper around {@link SerialProvisioner} for the Add Device wizard.
 * Keeps a single live serial connection across the connect -> configure steps.
 */
export function useSerialProvisioning() {
  const provisionerRef = useRef<SerialProvisioner | null>(null);
  const [state, setState] = useState<State>(INITIAL);

  const connect = useCallback(async () => {
    setState({ status: "connecting", info: null, error: null });
    try {
      const provisioner = new SerialProvisioner();
      provisionerRef.current = provisioner;
      await provisioner.connect();
      const info = await provisioner.getInfo();
      setState({ status: "connected", info, error: null });
      return info;
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to connect to the device.";
      await provisionerRef.current?.disconnect();
      provisionerRef.current = null;
      setState({ status: "error", info: null, error: message });
      throw err;
    }
  }, []);

  const sendConfig = useCallback(async (config: NetworkConfigInput) => {
    const provisioner = provisionerRef.current;
    if (!provisioner) throw new Error("Not connected to a device.");
    setState((s) => ({ ...s, status: "configuring", error: null }));
    try {
      await provisioner.setConfig(config);
      setState((s) => ({ ...s, status: "configured" }));
    } catch (err) {
      const message = err instanceof Error ? err.message : "Failed to configure the device.";
      setState((s) => ({ ...s, status: "error", error: message }));
      throw err;
    }
  }, []);

  const disconnect = useCallback(async () => {
    await provisionerRef.current?.disconnect();
    provisionerRef.current = null;
  }, []);

  const reset = useCallback(async () => {
    await provisionerRef.current?.disconnect();
    provisionerRef.current = null;
    setState(INITIAL);
  }, []);

  return {
    ...state,
    supported: isWebSerialSupported(),
    connect,
    sendConfig,
    disconnect,
    reset,
  };
}
