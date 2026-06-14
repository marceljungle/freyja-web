import { useQuery } from "@tanstack/react-query";
import { deviceApi } from "@/infrastructure/api/deviceApi";

export const deviceKeys = {
  all: ["devices"] as const,
  detail: (id: string) => ["devices", id] as const,
};

export function useDevices() {
  return useQuery({
    queryKey: deviceKeys.all,
    queryFn: () => deviceApi.list(),
  });
}

export function useDevice(deviceId: string | undefined) {
  return useQuery({
    queryKey: deviceKeys.detail(deviceId ?? "unknown"),
    queryFn: () => deviceApi.get(deviceId as string),
    enabled: Boolean(deviceId),
  });
}
