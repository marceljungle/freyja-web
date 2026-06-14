export interface Telemetry {
  id: number;
  deviceId: string;
  reason: string | null;
  hasFix: boolean;
  latitude: number | null;
  longitude: number | null;
  accuracy: number | null;
  batteryMv: number | null;
  batteryPercent: number | null;
  deviceTime: string | null;
  receivedAt: string;
}

export interface TrajectoryQuery {
  from?: string;
  to?: string;
  limit?: number;
}
