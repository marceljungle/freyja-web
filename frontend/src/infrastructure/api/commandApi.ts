import { apiClient } from "@/infrastructure/http/apiClient";
import type { DeviceCommand } from "@/domain/command";

export const commandApi = {
  /** Queue a "request location" command for a sleeping device (Flow 4). */
  async requestLocation(deviceId: string): Promise<DeviceCommand> {
    const { data } = await apiClient.post<DeviceCommand>(
      `/devices/${deviceId}/commands/request-location`,
    );
    return data;
  },
};
