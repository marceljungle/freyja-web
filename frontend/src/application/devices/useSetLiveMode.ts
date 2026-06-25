import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deviceApi } from "@/infrastructure/api/deviceApi";
import type { Device } from "@/domain/device";
import { deviceKeys } from "./useDevices";

interface LiveModeInput {
  enabled: boolean;
  persistent?: boolean;
  interval?: number;
}

/** Toggles live (real-time) mode on a device (publishes live_on / live_off). */
export function useSetLiveMode(deviceId: string | undefined) {
  const queryClient = useQueryClient();
  return useMutation<Device, Error, LiveModeInput>({
    mutationFn: (input) => deviceApi.setLiveMode(deviceId as string, input),
    onSuccess: (device) => {
      queryClient.setQueryData(deviceKeys.detail(device.id), device);
      void queryClient.invalidateQueries({ queryKey: deviceKeys.all });
    },
  });
}

/** True while live mode is active: persistent, or within a bounded window. */
export function isLiveModeActive(device: Device | undefined): boolean {
  if (!device) return false;
  if (device.livePersistent) return true;
  return device.liveModeUntil != null && new Date(device.liveModeUntil).getTime() > Date.now();
}
