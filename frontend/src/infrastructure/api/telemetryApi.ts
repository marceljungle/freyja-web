import { apiClient } from "@/infrastructure/http/apiClient";
import type { Telemetry, TrajectoryQuery } from "@/domain/telemetry";

export const telemetryApi = {
  /** Latest reading, or null when the device has never reported (HTTP 204). */
  async latest(deviceId: string): Promise<Telemetry | null> {
    const response = await apiClient.get<Telemetry | "">(`/devices/${deviceId}/telemetry/latest`);
    if (response.status === 204 || !response.data) return null;
    return response.data as Telemetry;
  },

  async trajectory(deviceId: string, query: TrajectoryQuery = {}): Promise<Telemetry[]> {
    const { data } = await apiClient.get<Telemetry[]>(`/devices/${deviceId}/telemetry`, {
      params: query,
    });
    return data;
  },
};
