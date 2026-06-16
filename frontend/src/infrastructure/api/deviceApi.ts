import { apiClient } from "@/infrastructure/http/apiClient";
import type { Device, RegisterDeviceInput } from "@/domain/device";

export const deviceApi = {
  async list(): Promise<Device[]> {
    const { data } = await apiClient.get<Device[]>("/devices");
    return data;
  },

  async get(deviceId: string): Promise<Device> {
    const { data } = await apiClient.get<Device>(`/devices/${deviceId}`);
    return data;
  },

  async register(input: RegisterDeviceInput): Promise<Device> {
    const { data } = await apiClient.post<Device>("/devices", input);
    return data;
  },

  async remove(deviceId: string): Promise<void> {
    await apiClient.delete(`/devices/${deviceId}`);
  },
};
