import { useQuery } from "@tanstack/react-query";
import { telemetryApi } from "@/infrastructure/api/telemetryApi";
import type { TrajectoryQuery } from "@/domain/telemetry";
import { TELEMETRY_POLL_INTERVAL_MS } from "@/shared/config";

export const telemetryKeys = {
  latest: (deviceId: string) => ["telemetry", deviceId, "latest"] as const,
  trajectory: (deviceId: string, query: TrajectoryQuery) =>
    ["telemetry", deviceId, "trajectory", query] as const,
};

/** Polls the latest position for near-real-time dashboard updates. */
export function useLatestTelemetry(deviceId: string | undefined) {
  return useQuery({
    queryKey: telemetryKeys.latest(deviceId ?? "unknown"),
    queryFn: () => telemetryApi.latest(deviceId as string),
    enabled: Boolean(deviceId),
    refetchInterval: TELEMETRY_POLL_INTERVAL_MS,
  });
}

export function useTrajectory(deviceId: string | undefined, query: TrajectoryQuery) {
  return useQuery({
    queryKey: telemetryKeys.trajectory(deviceId ?? "unknown", query),
    queryFn: () => telemetryApi.trajectory(deviceId as string, query),
    enabled: Boolean(deviceId),
  });
}
