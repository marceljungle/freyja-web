export interface Telemetry {
  id: number;
  deviceId: string;
  reason: string | null;
  hasFix: boolean;
  approximate: boolean;
  buffered: boolean;
  latitude: number | null;
  longitude: number | null;
  accuracy: number | null;
  batteryMv: number | null;
  batteryPercent: number | null;
  temperatureC: number | null;
  rsrp: number | null;
  trackedSvs: number | null;
  svsUsed: number | null;
  cn0: number | null;
  mcc: number | null;
  mnc: number | null;
  tac: number | null;
  cellId: number | null;
  deviceTime: string | null;
  receivedAt: string;
}

export interface TrajectoryQuery {
  from?: string;
  to?: string;
  limit?: number;
}
