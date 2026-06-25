export interface Device {
  id: string;
  imei: string;
  name: string;
  fwVersion: string | null;
  apn: string | null;
  brokerIp: string | null;
  brokerPort: number | null;
  lastSeenAt: string | null;
  liveModeUntil: string | null;
  createdAt: string;
}

/** Payload for registering a provisioned device (Flow 2, step 6). */
export interface RegisterDeviceInput {
  imei: string;
  name: string;
  fwVersion?: string | null;
  apn?: string | null;
  brokerIp?: string | null;
  brokerPort?: number | null;
}
