import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deviceApi } from "@/infrastructure/api/deviceApi";
import type { Device, RegisterDeviceInput } from "@/domain/device";
import { deviceKeys } from "./useDevices";

export function useRegisterDevice() {
  const queryClient = useQueryClient();
  return useMutation<Device, Error, RegisterDeviceInput>({
    mutationFn: (input) => deviceApi.register(input),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: deviceKeys.all });
    },
  });
}
