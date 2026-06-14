export type CommandStatus = "PENDING" | "SENT" | "ACKED" | "FAILED";

export interface DeviceCommand {
  id: string;
  deviceId: string;
  type: string;
  status: CommandStatus;
  createdAt: string;
  sentAt: string | null;
  acknowledgedAt: string | null;
}
