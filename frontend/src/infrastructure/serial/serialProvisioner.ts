import { SERIAL_BAUD_RATE } from "@/shared/config";

export interface DeviceInfo {
  imei: string;
  fwVersion: string;
}

export interface NetworkConfigInput {
  apn: string;
  brokerIp: string;
  brokerPort: number;
}

interface PendingMatcher {
  match: (value: Record<string, unknown>) => boolean;
  resolve: (value: Record<string, unknown>) => void;
  reject: (reason: Error) => void;
  timer: ReturnType<typeof setTimeout>;
}

const RESPONSE_TIMEOUT_MS = 8_000;

/** True when the browser exposes the Web Serial API (Chromium, secure context). */
export function isWebSerialSupported(): boolean {
  return typeof navigator !== "undefined" && "serial" in navigator;
}

/**
 * A human-readable reason why Web Serial is unavailable, or null when it works.
 * Distinguishes the common "insecure context" case (a LAN IP over plain HTTP)
 * from an unsupported browser, so the UI can guide the user to the right fix.
 */
export function webSerialUnavailableReason(): string | null {
  if (isWebSerialSupported()) {
    return null;
  }
  if (typeof window !== "undefined" && window.isSecureContext === false) {
    return (
      "Web Serial needs a secure context. Open this app over https:// or via " +
      "http://localhost (an SSH tunnel works), or start the dev server with HTTPS " +
      "(npm run dev:https)."
    );
  }
  return "Web Serial isn't supported in this browser. Use Chrome or Edge on desktop.";
}

/**
 * Drives the firmware's Web Serial onboarding protocol (see serial_cli.c):
 *   get_info  -> {"imei","fw_version"}
 *   set_config-> {"status":"ok"}  (device then reboots)
 *
 * The firmware also emits human-readable log lines on the same UART, so the read
 * loop ignores any line that is not JSON matching the expected response shape.
 */
export class SerialProvisioner {
  private port: SerialPort | null = null;
  private reader: ReadableStreamDefaultReader<Uint8Array> | null = null;
  private writer: WritableStreamDefaultWriter<Uint8Array> | null = null;
  private buffer = "";
  private readonly pending: PendingMatcher[] = [];
  private reading = false;

  async connect(): Promise<void> {
    if (!isWebSerialSupported()) {
      throw new Error("Web Serial is not supported in this browser (use Chrome or Edge over HTTPS/localhost).");
    }
    this.port = await navigator.serial.requestPort();
    await this.port.open({ baudRate: SERIAL_BAUD_RATE });
    if (!this.port.writable || !this.port.readable) {
      throw new Error("Serial port is not readable/writable.");
    }
    this.writer = this.port.writable.getWriter();
    void this.startReadLoop();
  }

  async getInfo(): Promise<DeviceInfo> {
    const waiting = this.waitFor(
      (o) => typeof o.imei === "string" && typeof o.fw_version === "string",
    );
    await this.send({ cmd: "get_info" });
    const response = await waiting;
    return { imei: response.imei as string, fwVersion: response.fw_version as string };
  }

  async setConfig(config: NetworkConfigInput): Promise<void> {
    const waiting = this.waitFor((o) => typeof o.status === "string");
    await this.send({
      cmd: "set_config",
      apn: config.apn,
      broker_ip: config.brokerIp,
      broker_port: config.brokerPort,
    });
    const response = await waiting;
    if (response.status !== "ok") {
      throw new Error("The device rejected the configuration.");
    }
  }

  async disconnect(): Promise<void> {
    this.reading = false;
    this.pending.splice(0).forEach((p) => {
      clearTimeout(p.timer);
      p.reject(new Error("Serial connection closed."));
    });
    try {
      await this.reader?.cancel();
    } catch {
      /* ignore */
    }
    try {
      this.reader?.releaseLock();
    } catch {
      /* ignore */
    }
    try {
      this.writer?.releaseLock();
    } catch {
      /* ignore */
    }
    try {
      await this.port?.close();
    } catch {
      /* the device reboots after set_config, which can close the port abruptly */
    }
    this.port = null;
    this.reader = null;
    this.writer = null;
  }

  private async startReadLoop(): Promise<void> {
    if (!this.port?.readable) return;
    this.reader = this.port.readable.getReader();
    const decoder = new TextDecoder();
    this.reading = true;
    try {
      while (this.reading) {
        const { value, done } = await this.reader.read();
        if (done) break;
        if (value) {
          this.buffer += decoder.decode(value, { stream: true });
          this.drainLines();
        }
      }
    } catch {
      /* reader cancelled during disconnect */
    } finally {
      try {
        this.reader?.releaseLock();
      } catch {
        /* ignore */
      }
    }
  }

  private drainLines(): void {
    let newline: number;
    while ((newline = this.buffer.indexOf("\n")) >= 0) {
      const line = this.buffer.slice(0, newline).trim();
      this.buffer = this.buffer.slice(newline + 1);
      if (line) this.handleLine(line);
    }
  }

  private handleLine(line: string): void {
    let parsed: Record<string, unknown>;
    try {
      parsed = JSON.parse(line);
    } catch {
      return; // not JSON: a firmware log line, ignore it
    }
    const index = this.pending.findIndex((p) => p.match(parsed));
    if (index >= 0) {
      const [matcher] = this.pending.splice(index, 1);
      clearTimeout(matcher.timer);
      matcher.resolve(parsed);
    }
  }

  private waitFor(
    match: (value: Record<string, unknown>) => boolean,
  ): Promise<Record<string, unknown>> {
    return new Promise((resolve, reject) => {
      const timer = setTimeout(() => {
        const i = this.pending.findIndex((p) => p.timer === timer);
        if (i >= 0) this.pending.splice(i, 1);
        reject(new Error("Timed out waiting for the device to respond."));
      }, RESPONSE_TIMEOUT_MS);
      this.pending.push({ match, resolve, reject, timer });
    });
  }

  private async send(payload: Record<string, unknown>): Promise<void> {
    if (!this.writer) throw new Error("Serial connection is not open.");
    const bytes = new TextEncoder().encode(`${JSON.stringify(payload)}\n`);
    await this.writer.write(bytes);
  }
}
