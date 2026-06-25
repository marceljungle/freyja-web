import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deviceApi } from "@/infrastructure/api/deviceApi";
import type { Device } from "@/domain/device";
import { deviceKeys } from "./useDevices";

interface LiveModeInput {
  enabled: boolean;
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
