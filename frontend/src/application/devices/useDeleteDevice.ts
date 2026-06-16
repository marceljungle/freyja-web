import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deviceApi } from "@/infrastructure/api/deviceApi";
import { deviceKeys } from "./useDevices";

/** Deletes a device (and its telemetry/commands) and refreshes the device list. */
export function useDeleteDevice() {
  const queryClient = useQueryClient();
  return useMutation<void, Error, string>({
    mutationFn: (deviceId) => deviceApi.remove(deviceId),
    onSuccess: (_data, deviceId) => {
      queryClient.removeQueries({ queryKey: deviceKeys.detail(deviceId) });
      void queryClient.invalidateQueries({ queryKey: deviceKeys.all });
    },
  });
}
