import { useMutation, useQueryClient } from "@tanstack/react-query";
import { commandApi } from "@/infrastructure/api/commandApi";
import type { DeviceCommand } from "@/domain/command";

/** Requests an on-demand location from a sleeping device (Flow 4). */
export function useRequestLocation(deviceId: string | undefined) {
  const queryClient = useQueryClient();
  return useMutation<DeviceCommand, Error, void>({
    mutationFn: () => commandApi.requestLocation(deviceId as string),
    onSuccess: () => {
      if (deviceId) {
        void queryClient.invalidateQueries({ queryKey: ["telemetry", deviceId] });
      }
    },
  });
}
